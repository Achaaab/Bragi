package com.github.achaaab.bragi.codec.flac;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * FLAC input stream
 *
 * @author Project Nayuki
 * @author Jonathan Guéhenneux
 * @since 0.1.7
 */
public class FlacInputStream implements AutoCloseable {

	private final InputStream inputStream;

	private long buffer;
	private int bufferLength;

	/**
	 * @param inputStream underlying input stream to read from
	 */
	public FlacInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	/**
	 * Skips the last bits of the current byte.
	 */
	public void alignToByte() {
		bufferLength -= bufferLength % 8;
	}

	/**
	 * Reads a single byte from the underlying input stream.
	 *
	 * @return read byte
	 * @throws IOException I/O exception while reading a byte
	 */
	public int readByte() throws IOException {

		if (bufferLength >= 8) {
			return readUnsignedInteger(8);
		} else {
			return inputStream.read();
		}
	}

	/**
	 * Reads bytes from the underlying input stream.
	 *
	 * @param length number of bytes to read
	 * @return read bytes
	 * @throws IOException I/O exception while reading
	 */
	public byte[] readBytes(int length) throws IOException {
		return inputStream.readNBytes(length);
	}

	/**
	 * Skips {@code length} bytes.
	 *
	 * @param length number of bytes to skip
	 * @throws IOException I/O exception while skipping bytes
	 */
	public void skip(int length) throws IOException {

		while (length > 0) {

			readByte();
			length--;
		}
	}

	/**
	 * Reads an unsigned integer.
	 *
	 * @param length number of bits of the unsigned integer to read
	 * @return read unsigned integer of specified length
	 * @throws IOException I/O exception while reading an unsigned integer
	 */
	public int readUnsignedInteger(int length) throws IOException {

		while (bufferLength < length) {

			var octet = inputStream.read();

			if (octet == -1) {
				throw new EOFException();
			}

			buffer = (buffer << 8) | octet;
			bufferLength += 8;
		}

		bufferLength -= length;

		var unsignedInteger = (int) (buffer >>> bufferLength);

		if (length < 32) {
			unsignedInteger &= (1 << length) - 1;
		}

		return unsignedInteger;
	}

	/**
	 * Reads a signed integer.
	 *
	 * @param length number of bits of the signed integer to read
	 * @return read signed integer
	 * @throws IOException I/O exception while reading a signed integer
	 */
	public int readSignedInt(int length) throws IOException {
		return (readUnsignedInteger(length) << (32 - length)) >> (32 - length);
	}

	/**
	 * more documentation on <a href="https://fr.wikipedia.org/wiki/Codage_de_Rice">Codage de Rice</a>
	 *
	 * @param k
	 * @return
	 * @throws IOException
	 */
	public long readRiceSignedInteger(int k) throws IOException {

		var val = 0L;

		while (readUnsignedInteger(1) == 0) {
			val++;
		}

		val = (val << k) | readUnsignedInteger(k);

		return (val >>> 1) ^ -(val & 1);
	}

	/**
	 * Reads a 32-bits unsigned integer in little endian order.
	 *
	 * @return read integer
	 * @throws IOException I/O exception while reading an integer
	 */
	public long readLittleEndianUnsignedInteger() throws IOException {

		long byte0 = readByte();
		long byte1 = readByte();
		long byte2 = readByte();
		long byte3 = readByte();

		if ((byte0 | byte1 | byte2 | byte3) < 0) {
			throw new EOFException();
		}

		return byte0 + (byte1 << 8) + (byte2 << 16) + (byte3 << 24);
	}

	/**
	 * Reads a 32-bits unsigned integer in big endian order.
	 *
	 * @return read integer
	 * @throws IOException I/O exception while reading an integer
	 */
	public long readBigEndianUnsignedInteger() throws IOException {

		long byte0 = readByte();
		long byte1 = readByte();
		long byte2 = readByte();
		long byte3 = readByte();

		if ((byte0 | byte1 | byte2 | byte3) < 0) {
			throw new EOFException();
		}

		return byte3 + (byte2 << 8) + (byte1 << 16) + (byte0 << 24);
	}

	/**
	 * Reads a string encoded with US-ASCII charset.
	 * Reads but does not returns last NUL characters.
	 *
	 * @param length           number of bytes to read
	 * @param stripTrailingNul whether to strip trailing NUL characters
	 * @return read string
	 * @throws IOException I/O exception while reading a string
	 */
	public String readAsciiString(int length, boolean stripTrailingNul) throws IOException {

		var bytes = readBytes(length);

		if (stripTrailingNul) {

			while (bytes[length - 1] == 0) {
				length--;
			}
		}

		return new String(bytes, 0, length, US_ASCII);
	}

	/**
	 * Reads a string encoded with UTF-8 charset.
	 *
	 * @param length number of bytes to read
	 * @return read string
	 * @throws IOException I/O exception while reading a string
	 */
	public String readUtf8String(int length) throws IOException {

		var bytes = readBytes(length);
		return new String(bytes, UTF_8);
	}

	/**
	 * Reads an unsigned, big-endian, 32-bits integer from the given input and then decode an US-ASCII string of
	 * found length from the same input.
	 *
	 * @return read string
	 * @throws IOException          I/O exception while reading a string
	 * @throws FlacDecoderException if string length is not supported
	 */
	public String decodeAsciiString() throws FlacDecoderException, IOException {

		var length = readBigEndianUnsignedInteger();

		if (length > Integer.MAX_VALUE) {

			throw new FlacDecoderException(
					"unsupported string length (" + length + "), maximum is " + Integer.MAX_VALUE);
		}

		return readAsciiString((int) length, false);
	}

	/**
	 * Reads an unsigned, big-endian, 32-bits integer from the given input and then decode an US-ASCII string of
	 * found length from the same input.
	 *
	 * @return read string
	 * @throws IOException          I/O exception while reading a string
	 * @throws FlacDecoderException if string length is not supported
	 */
	public String decodeUtf8String() throws FlacDecoderException, IOException {

		var length = readBigEndianUnsignedInteger();

		if (length > Integer.MAX_VALUE) {

			throw new FlacDecoderException(
					"unsupported string length (" + length + "), maximum is " + Integer.MAX_VALUE);
		}

		return readUtf8String((int) length);
	}

	/**
	 * Reads an unsigned, little-endian, 32-bits integer from the given input and then decode an UTF-8 string of
	 * found length from the same input.
	 *
	 * @return read string
	 * @throws IOException          I/O exception while reading a vorbis string
	 * @throws FlacDecoderException if string length is not supported
	 */
	public String decodeVorbisString() throws FlacDecoderException, IOException {

		var length = readLittleEndianUnsignedInteger();

		if (length > Integer.MAX_VALUE) {

			throw new FlacDecoderException(
					"unsupported string length (" + length + "), maximum is " + Integer.MAX_VALUE);
		}

		return readUtf8String((int) length);
	}

	/**
	 * @throws IOException I/O exception while closing this bit stream
	 */
	public void close() throws IOException {
		inputStream.close();
	}
}
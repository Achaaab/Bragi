package com.github.achaaab.bragi.codec.flac;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Project Nayuki
 * @author Jonathan Guéhenneux
 * @since 0.1.7
 */
public class BitInputStream implements AutoCloseable {

	private final InputStream inputStream;

	private long buffer;
	private int bufferLength;

	/**
	 * @param inputStream underlying input stream to read from
	 */
	public BitInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	/**
	 *
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
	 * Reads an unsigned integer.
	 *
	 * @param length number of bits of the unsigned integer to read
	 * @return read unsigned integer of specified length
	 * @throws IOException I/O exception while reading an unsigned integer
	 */
	public int readUnsignedInteger(int length) throws IOException {

		while (bufferLength < length) {

			int eightBits = inputStream.read();

			if (eightBits == -1) {
				throw new EOFException();
			}

			buffer = (buffer << 8) | eightBits;
			bufferLength += 8;
		}

		bufferLength -= length;

		int result = (int) (buffer >>> bufferLength);

		if (length < 32) {
			result &= (1 << length) - 1;
		}

		return result;
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
	 * more documentation on https://fr.wikipedia.org/wiki/Codage_de_Rice
	 *
	 * @param param
	 * @return
	 * @throws IOException
	 */
	public long readRiceSignedInteger(int param) throws IOException {

		var val = 0L;

		while (readUnsignedInteger(1) == 0) {
			val++;
		}

		val = (val << param) | readUnsignedInteger(param);

		return (val >>> 1) ^ -(val & 1);
	}

	/**
	 * @throws IOException I/O exception while closing this bit stream
	 */
	public void close() throws IOException {
		inputStream.close();
	}
}
package com.github.achaaab.bragi.file;

import com.github.achaaab.bragi.common.Normalizer;
import com.github.achaaab.bragi.common.Settings;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;

import static java.nio.ByteBuffer.wrap;
import static java.nio.ByteOrder.LITTLE_ENDIAN;
import static java.nio.charset.StandardCharsets.US_ASCII;

/**
 * WAV file
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.0
 */
public class WavFile implements AutoCloseable {

	private static final int HEADER_INDEX = 0;
	private static final int HEADER_SIZE = 44;
	private static final ByteOrder HEADER_BYTE_ORDER = LITTLE_ENDIAN;
	private static final String READ_ONLY_MODE = "r";

	private static final int ONE_BYTE_MIN_VALUE = 0xFF_FF_FF_A0;
	private static final int ONE_BYTE_MAX_VALUE = 0x00_00_00_7F;

	private static final int TWO_BYTES_MIN_VALUE = 0xFF_FF_80_00;
	private static final int TWO_BYTES_MAX_VALUE = 0x00_00_7F_FF;

	private static final Normalizer ONE_BYTE_NORMALIZER = new Normalizer(
			ONE_BYTE_MIN_VALUE, ONE_BYTE_MAX_VALUE,
			Settings.INSTANCE.minimalVoltage(), Settings.INSTANCE.maximalVoltage()
	);

	private static final Normalizer TWO_BYTES_NORMALIZER = new Normalizer(
			TWO_BYTES_MIN_VALUE, TWO_BYTES_MAX_VALUE,
			Settings.INSTANCE.minimalVoltage(), Settings.INSTANCE.maximalVoltage()
	);

	private final RandomAccessFile fileReader;

	private WavFileHeader header;

	/**
	 * @param file file
	 * @throws IOException               I/O error while reading header
	 * @throws MalformedWavFileException if header is malformed
	 */
	public WavFile(File file) throws IOException, MalformedWavFileException {

		fileReader = new RandomAccessFile(file, READ_ONLY_MODE);

		readHeader();
	}

	/**
	 * Reads samples from this WAV file at specified offset and writes them in given samples array.
	 *
	 * @param samples array where to write read samples
	 * @param offset  offset from which to read
	 * @return number of read bytes
	 * @throws IOException I/O error while reading samples
	 */
	public int read(float[][] samples, int offset) throws IOException {

		var frameCount = samples[0].length;

		fileReader.seek(HEADER_SIZE + offset);
		var bytes = new byte[frameCount * header.frameSize()];
		var byteCount = fileReader.read(bytes);
		var buffer = wrap(bytes);
		buffer.order(LITTLE_ENDIAN);

		frameCount = byteCount / header.frameSize();
		var bytesPerSample = header.sampleSize() / 8;

		var index = 0;

		for (var frameIndex = 0; frameIndex < frameCount; frameIndex++) {

			for (var channelIndex = 0; channelIndex < header.channelCount(); channelIndex++) {

				samples[channelIndex][frameIndex] = switch (bytesPerSample) {

					case 1 -> ONE_BYTE_NORMALIZER.normalize(buffer.get(index));
					case 2 -> TWO_BYTES_NORMALIZER.normalize(buffer.getShort(index));

					default -> throw new IllegalArgumentException(
							"unsupported sample size: " + bytesPerSample + " bytes");
				};

				index += bytesPerSample;
			}
		}

		return byteCount;
	}

	/**
	 * @return header of this WAV file
	 */
	public WavFileHeader getHeader() {
		return header;
	}

	/**
	 * Reads the header of this WAV file.
	 *
	 * @throws IOException               I/O error while reading header
	 * @throws MalformedWavFileException if header is malformed
	 */
	private void readHeader() throws IOException, MalformedWavFileException {

		fileReader.seek(HEADER_INDEX);

		var bytes = new byte[HEADER_SIZE];
		var count = fileReader.read(bytes);

		if (count < HEADER_SIZE) {
			throw new MalformedWavFileException("incomplete header: " + count + " bytes / " + HEADER_SIZE);
		}

		var buffer = wrap(bytes);

		// strings are read with big endian order, but numbers (shorts and ints) are read with little endian order
		buffer.order(HEADER_BYTE_ORDER);

		var offset = 0;

		// all strings in header are 4 bytes long
		var stringBytes = new byte[4];

		// file type: constant "RIFF" (Resource Interchange File Format)
		// TODO check constant value
		buffer.get(offset, stringBytes, 0, 4);
		var fileType = new String(stringBytes, US_ASCII);
		offset += 4;

		// file size in bytes minus 8 bytes (fileType and fileSize)
		// TODO check file size
		// TODO file size is actually an unsigned int and should be stored in a long
		var fileSize = buffer.getInt(offset);
		offset += 4;

		// file format: constant "WAVE"
		// TODO check constant value
		buffer.get(offset, stringBytes, 0, 4);
		var fileFormat = new String(stringBytes, US_ASCII);
		offset += 4;

		// format chunk title: constant "fmt "
		// TODO check constant value
		buffer.get(offset, stringBytes, 0, 4);
		var formatChunkTitle = new String(stringBytes, US_ASCII);
		offset += 4;

		// format chunk size in bytes: constant 16
		// TODO check constant value
		var formatChunkSize = buffer.getInt(offset);
		offset += 4;

		/*
		audio format: only 1 is supported
		1: PCM (Pulse Code Modulation) = linear quantization
		TODO check that audio format is PCM
		 */
		var audioFormat = buffer.getShort(offset);
		offset += 2;

		/*
		channel count: number of channels
		1: mono
		2: stereo
		etc.
		 */
		var channelCount = buffer.getShort(offset);
		offset += 2;

		// frame rate: number of frames per second
		// TODO check frame rate (must be strictly positive)
		var frameRate = buffer.getInt(offset);
		offset += 4;

		// byte rate: number of bytes per second
		// TODO check byte rate (must be equal to frame rate * frame size)
		var byteRate = buffer.getInt(offset);
		offset += 4;

		// frame size: number of bytes per frame
		// TODO check frame size (must be equal to channel count * sample size / 8)
		var frameSize = buffer.getShort(offset);
		offset += 2;

		// sample size: number of bits per sample, multiple of 8
		// TODO only 8 and 16 are supported, add support for 24 and 32 bits
		var sampleSize = buffer.getShort(offset);
		offset += 2;

		// data chunk title: constant "data"
		// TODO check constant value
		buffer.get(offset, stringBytes, 0, 4);
		var dataChunkTitle = new String(stringBytes, US_ASCII);
		offset += 4;

		// data size: size of data in bytes
		// TODO check value
		var dataSize = buffer.getInt(offset);

		header = new WavFileHeader(fileType, fileSize, fileFormat, formatChunkTitle, formatChunkSize, audioFormat,
				channelCount, frameRate, byteRate, frameSize, sampleSize, dataChunkTitle, dataSize);
	}

	@Override
	public void close() throws IOException {
		fileReader.close();
	}
}
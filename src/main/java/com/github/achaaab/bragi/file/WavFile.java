package com.github.achaaab.bragi.file;

import com.github.achaaab.bragi.common.Settings;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;
import java.nio.file.Path;

import static java.lang.Math.min;
import static java.lang.Math.round;
import static java.nio.ByteBuffer.wrap;
import static java.nio.ByteOrder.LITTLE_ENDIAN;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * WAV file
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.0
 */
public class WavFile implements AudioFile {

	private static final Logger LOGGER = getLogger(WavFile.class);

	private static final int HEADER_SIZE = 44;
	private static final ByteOrder HEADER_BYTE_ORDER = LITTLE_ENDIAN;
	private static final String READ_ONLY_MODE = "r";

	private final Path path;

	private RandomAccessFile reader;
	private int offset;
	private WavFileHeader header;
	private float duration;

	/**
	 * @param path path to a WAV file
	 * @since 0.2.0
	 */
	public WavFile(Path path) {
		this.path = path;
	}

	@Override
	public void open() throws AudioFileException {

		try {

			LOGGER.info("opening WAV file {}", path);

			reader = new RandomAccessFile(path.toFile(), READ_ONLY_MODE);
			offset = 0;

			readHeader();

			float dataSize = header.dataSize();
			var byteRate = header.byteRate();

			duration = dataSize / byteRate;

		} catch (IOException cause) {

			throw new AudioFileException(cause);
		}
	}

	@Override
	public void close() throws AudioFileException {

		try {

			LOGGER.info("closing WAV file {}", path);
			reader.close();

		} catch (IOException cause) {

			throw new AudioFileException(cause);
		}
	}

	@Override
	public float time() {
		return (float) offset / header.byteRate();
	}

	@Override
	public float duration() {
		return duration;
	}

	@Override
	public void seekTime(double targetTime) {

		var bytes = (float) targetTime * header.byteRate();
		var frameIndex = round(bytes / header.frameSize());

		offset = frameIndex * header.frameSize();
	}

	@Override
	public float[][] readChunk() throws AudioFileException {

		float[][] chunk = null;

		if (offset < header.dataSize()) {

			try {

				var frameRate = header.frameRate();
				var chunkDuration = Settings.INSTANCE.chunkDuration();
				var frameCount = round(frameRate * chunkDuration);
				var frameSize = header.frameSize();
				var channelCount = header.channelCount();
				var byteCount = min(frameCount * frameSize, header.dataSize() - offset);

				var bytes = new byte[byteCount];

				reader.seek(HEADER_SIZE + offset);
				byteCount = reader.read(bytes);
				offset += byteCount;

				var buffer = wrap(bytes);
				buffer.order(LITTLE_ENDIAN);
				var bufferIndex = 0;

				frameCount = byteCount / header.frameSize();
				var sampleSizeInBytes = header.sampleSize() / 8;

				chunk = new float[channelCount][frameCount];

				for (var frameIndex = 0; frameIndex < frameCount; frameIndex++) {

					for (var channelIndex = 0; channelIndex < channelCount; channelIndex++) {

						chunk[channelIndex][frameIndex] = switch (sampleSizeInBytes) {

							case 1 -> ONE_BYTE_NORMALIZER.normalize(buffer.get(bufferIndex));
							case 2 -> TWO_BYTES_NORMALIZER.normalize(buffer.getShort(bufferIndex));
							default -> throw new AudioFileException(
									"unsupported sample size: " + sampleSizeInBytes + " bytes");
						};

						bufferIndex += sampleSizeInBytes;
					}
				}

			} catch (IOException cause) {

				throw new AudioFileException(cause);
			}
		}

		return chunk;
	}

	@Override
	public float sampleRate() {
		return header.frameRate();
	}

	/**
	 * Reads the header of this WAV file.
	 *
	 * @throws AudioFileException exception while reading the header of this WAV file
	 * @since 0.2.0
	 */
	private void readHeader() throws AudioFileException {

		var bytes = new byte[HEADER_SIZE];

		try {

			reader.seek(0);
			reader.readFully(bytes);

		} catch (IOException cause) {

			throw new AudioFileException(cause);
		}

		var buffer = wrap(bytes);

		// strings are read with big endian order, but numbers (shorts and ints) are read with little endian order
		buffer.order(HEADER_BYTE_ORDER);

		var headerOffset = 0;

		// all strings in header are 4 bytes long
		var stringBytes = new byte[4];

		// file type: constant "RIFF" (Resource Interchange File Format)
		// TODO check constant value
		buffer.get(headerOffset, stringBytes, 0, 4);
		var fileType = new String(stringBytes, US_ASCII);
		headerOffset += 4;

		// file size in bytes minus 8 bytes (fileType and fileSize)
		// TODO check file size
		// TODO file size is actually an unsigned int and should be stored in a long
		var fileSize = buffer.getInt(headerOffset);
		headerOffset += 4;

		// file format: constant "WAVE"
		// TODO check constant value
		buffer.get(headerOffset, stringBytes, 0, 4);
		var fileFormat = new String(stringBytes, US_ASCII);
		headerOffset += 4;

		// format chunk title: constant "fmt "
		// TODO check constant value
		buffer.get(headerOffset, stringBytes, 0, 4);
		var formatChunkTitle = new String(stringBytes, US_ASCII);
		headerOffset += 4;

		// format chunk size in bytes: constant 16
		// TODO check constant value
		var formatChunkSize = buffer.getInt(headerOffset);
		headerOffset += 4;

		/*
		audio format: only 1 is supported
		1: PCM (Pulse Code Modulation) = linear quantization
		TODO check that audio format is PCM
		 */
		var audioFormat = buffer.getShort(headerOffset);
		headerOffset += 2;

		/*
		channel count: number of channels
		1: mono
		2: stereo
		etc.
		 */
		var channelCount = buffer.getShort(headerOffset);
		headerOffset += 2;

		// frame rate: number of frames per second
		// TODO check frame rate (must be strictly positive)
		var frameRate = buffer.getInt(headerOffset);
		headerOffset += 4;

		// byte rate: number of bytes per second
		// TODO check byte rate (must be equal to frame rate * frame size)
		var byteRate = buffer.getInt(headerOffset);
		headerOffset += 4;

		// frame size: number of bytes per frame
		// TODO check frame size (must be equal to channel count * sample size / 8)
		var frameSize = buffer.getShort(headerOffset);
		headerOffset += 2;

		// sample size: number of bits per sample, multiple of 8
		// TODO only 8, 16 and 24 are supported, add support for 32 bits
		var sampleSize = buffer.getShort(headerOffset);
		headerOffset += 2;

		// data chunk title: constant "data"
		// TODO check constant value
		buffer.get(headerOffset, stringBytes, 0, 4);
		var dataChunkTitle = new String(stringBytes, US_ASCII);
		headerOffset += 4;

		// data size: size of data in bytes
		// TODO check value
		var dataSize = buffer.getInt(headerOffset);

		header = new WavFileHeader(fileType, fileSize, fileFormat, formatChunkTitle, formatChunkSize, audioFormat,
				channelCount, frameRate, byteRate, frameSize, sampleSize, dataChunkTitle, dataSize);
	}
}

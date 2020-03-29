package com.github.achaaab.bragi.common;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @author Jonathan Guéhenneux
 */
public class WavFile {

	private static final String READ_ONLY_MODE = "r";
	public static final int WAV_HEADER_INDEX = 0;
	public static final int WAV_HEADER_SIZE = 44;

	private File file;
	private RandomAccessFile fileReader;
	private String fileType;
	private int fileSize;
	private String fileFormat;
	private String formatChunkTitle;
	private int formatChunkLength;
	private short audioFormat;
	private short channelCount;
	private int frameRate;
	private int byteRate;
	private short frameSizeInBytes;
	private short sampleSizeInBytes;
	private short frameSizeInBits;
	private String dataChunkTitle;
	private int dataLength;

	/**
	 * @param file
	 * @throws MalformedWavFileException
	 * @throws IOException
	 */
	public WavFile(File file) throws IOException, MalformedWavFileException {

		this.file = file;

		fileReader = new RandomAccessFile(file, READ_ONLY_MODE);
		readHeader();
	}

	/**
	 * @param samples
	 * @param offset
	 * @return
	 * @throws IOException
	 */
	public int read(float[][] samples, int offset) throws IOException {

		int frameCount = samples[0].length;
		fileReader.seek(WAV_HEADER_SIZE + offset);
		byte[] buffer = new byte[frameCount * frameSizeInBytes];
		int count = fileReader.read(buffer);
		frameCount = count / frameSizeInBytes;

		int frameIndex, channelIndex, sampleIndex;

		float sample;

		byte b0, b1;

		for (frameIndex = 0; frameIndex < frameCount; frameIndex++) {

			for (channelIndex = 0; channelIndex < channelCount; channelIndex++) {

				sampleIndex = frameIndex * frameSizeInBytes + channelIndex * sampleSizeInBytes;

				if (sampleSizeInBytes == 1) {

					b0 = buffer[sampleIndex + 0];
					sample = (float) b0 / Byte.MAX_VALUE;

				} else if (sampleSizeInBytes == 2) {

					b0 = buffer[sampleIndex + 0];
					b1 = buffer[sampleIndex + 1];

					sample = (float) (b0 & 0xFF | b1 << 8) / Short.MAX_VALUE;

				} else {

					sample = 0.0f;
				}

				samples[channelIndex][frameIndex] = sample;
			}
		}

		return count;
	}

	/**
	 * @return the file
	 */
	public File getFile() {
		return file;
	}

	/**
	 * @return the fileReader
	 */
	public RandomAccessFile getFileReader() {
		return fileReader;
	}

	/**
	 * @return the fileType
	 */
	public String getFileType() {
		return fileType;
	}

	/**
	 * @return the fileSize
	 */
	public int getFileSize() {
		return fileSize;
	}

	/**
	 * @return the fileFormat
	 */
	public String getFileFormat() {
		return fileFormat;
	}

	/**
	 * @return the formatChunkTitle
	 */
	public String getFormatChunkTitle() {
		return formatChunkTitle;
	}

	/**
	 * @return the formatChunkLength
	 */
	public int getFormatChunkLength() {
		return formatChunkLength;
	}

	/**
	 * @return the audioFormat
	 */
	public short getAudioFormat() {
		return audioFormat;
	}

	/**
	 * @return the channelCount
	 */
	public short getChannelCount() {
		return channelCount;
	}

	/**
	 * @return the frameRate
	 */
	public int getFrameRate() {
		return frameRate;
	}

	/**
	 * @return the byteRate
	 */
	public int getByteRate() {
		return byteRate;
	}

	/**
	 * @return the frameSizeInBytes
	 */
	public short getFrameSizeInBytes() {
		return frameSizeInBytes;
	}

	/**
	 * @return the frameSizeInBits
	 */
	public short getFrameSizeInBits() {
		return frameSizeInBits;
	}

	/**
	 * @return the dataChunkTitle
	 */
	public String getDataChunkTitle() {
		return dataChunkTitle;
	}

	/**
	 * @return the dataLength
	 */
	public int getDataLength() {
		return dataLength;
	}

	/**
	 * @throws IOException
	 * @throws MalformedWavFileException
	 */
	private void readHeader() throws IOException, MalformedWavFileException {

		fileReader.seek(WAV_HEADER_INDEX);
		byte[] header = new byte[WAV_HEADER_SIZE];
		int count = fileReader.read(header);

		if (count < WAV_HEADER_SIZE) {
			throw new MalformedWavFileException("incomplete header : " + count + " bytes / " + WAV_HEADER_SIZE);
		}

		int offset = 0;
		int length;

		/*
		 * file type
		 */

		length = 4;
		fileType = ByteArrayUtils.getString(header, offset, length);
		offset += length;

		/*
		 * file size
		 */

		length = 4;
		fileSize = ByteArrayUtils.getInteger(header, offset, false);
		offset += length;

		/*
		 * file format
		 */

		length = 4;
		fileFormat = ByteArrayUtils.getString(header, offset, length);
		offset += length;

		/*
		 * format chunk title
		 */

		length = 4;
		formatChunkTitle = ByteArrayUtils.getString(header, offset, length);
		offset += length;

		/*
		 * format chunk length
		 */

		length = 4;
		formatChunkLength = ByteArrayUtils.getInteger(header, offset, false);
		offset += length;

		/*
		 * bragi format
		 */

		length = 2;
		audioFormat = ByteArrayUtils.getShort(header, offset, false);
		offset += length;

		/*
		 * channel count
		 */

		length = 2;
		channelCount = ByteArrayUtils.getShort(header, offset, false);
		offset += length;

		/*
		 * frame rate
		 */

		length = 4;
		frameRate = ByteArrayUtils.getInteger(header, offset, false);
		offset += length;

		/*
		 * byte rate
		 */

		length = 4;
		byteRate = ByteArrayUtils.getInteger(header, offset, false);
		offset += length;

		/*
		 * frame size in bytes
		 */

		length = 2;
		frameSizeInBytes = ByteArrayUtils.getShort(header, offset, false);
		offset += length;

		/*
		 * frame size in bits
		 */

		length = 2;
		frameSizeInBits = ByteArrayUtils.getShort(header, offset, false);
		offset += length;

		/*
		 * data chunk title
		 */

		length = 4;
		dataChunkTitle = ByteArrayUtils.getString(header, offset, length);
		offset += length;

		/*
		 * data length
		 */

		length = 4;
		dataLength = ByteArrayUtils.getInteger(header, offset, false);
		offset += length;

		sampleSizeInBytes = (short) (frameSizeInBytes / channelCount);
	}

	/**
	 * @throws IOException
	 */
	public final void close() throws IOException {
		fileReader.close();
	}
}
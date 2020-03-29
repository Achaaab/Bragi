package com.github.achaaab.bragi.common;

/**
 * @author Jonathan Guéhenneux
 * @since 0.0.1
 */
public class Settings {

	public static final Settings INSTANCE = new Settings();

	private static final int DEFAULT_CHANNELS_COUNT = 2;
	private static final int DEFAULT_SAMPLE_SIZE = 2;
	private static final int DEFAULT_FRAME_RATE = 44100;
	private static final int DEFAULT_CHUNK_SIZE = 100;

	private static final float DEFAULT_MINIMAL_VOLTAGE = -5.0f;
	private static final float DEFAULT_MAXIMAL_VOLTAGE = 5.0f;

	private int channelCount;
	private int sampleSize;
	private int frameRate;
	private int chunkSize;
	private float minimalVoltage;
	private float maximalVoltage;

	/**
	 * Create new default settings.
	 */
	private Settings() {

		channelCount = DEFAULT_CHANNELS_COUNT;
		frameRate = DEFAULT_FRAME_RATE;
		sampleSize = DEFAULT_SAMPLE_SIZE;
		chunkSize = DEFAULT_CHUNK_SIZE;
		minimalVoltage = DEFAULT_MINIMAL_VOLTAGE;
		maximalVoltage = DEFAULT_MAXIMAL_VOLTAGE;
	}

	/**
	 * @return number of channels
	 */
	public int getChannelCount() {
		return channelCount;
	}

	/**
	 * @param channelCount number of channels
	 */
	public void setChannelCount(int channelCount) {
		this.channelCount = channelCount;
	}

	/**
	 * @return number of frames per second
	 */
	public int getFrameRate() {
		return frameRate;
	}

	/**
	 * @param frameRate number of frames per second
	 */
	public void setFrameRate(int frameRate) {
		this.frameRate = frameRate;
	}

	/**
	 * @return number of bytes per sample
	 */
	public int getSampleSize() {
		return sampleSize;
	}

	/**
	 * @param sampleSize number of bytes per sample
	 */
	public void setSampleSize(int sampleSize) {
		this.sampleSize = sampleSize;
	}

	/**
	 * @return number of frames per chunk
	 */
	public int getChunkSize() {
		return chunkSize;
	}

	/**
	 * @return minimal voltage
	 */
	public float getMinimalVoltage() {
		return minimalVoltage;
	}

	/**
	 * @return maximal voltage
	 */
	public float getMaximalVoltage() {
		return maximalVoltage;
	}

	/**
	 * @return number of bytes per frame
	 */
	public int getFrameSize() {
		return channelCount * sampleSize;
	}

	/**
	 * @return frame length in seconds
	 */
	public double getFrameLength() {
		return 1.0 / frameRate;
	}

	/**
	 * @return number of bytes per second
	 */
	public int getByteRate() {
		return frameRate * getFrameSize();
	}
}
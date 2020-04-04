package com.github.achaaab.bragi.common;

/**
 * @author Jonathan Guéhenneux
 * @since 0.0.1
 */
public record Settings(

		int channelCount,
		int sampleSize,
		int frameRate,
		int chunkSize,
		float minimalVoltage,
		float maximalVoltage) {

	public static final Settings INSTANCE = new Settings();

	private static final int DEFAULT_CHANNELS_COUNT = 2;
	private static final int DEFAULT_SAMPLE_SIZE = 2;
	private static final int DEFAULT_FRAME_RATE = 44100;
	private static final int DEFAULT_CHUNK_SIZE = 100;
	private static final float DEFAULT_MINIMAL_VOLTAGE = -5.0f;
	private static final float DEFAULT_MAXIMAL_VOLTAGE = 5.0f;

	/**
	 * Create new default settings.
	 */
	private Settings() {

		this(
				DEFAULT_CHANNELS_COUNT,
				DEFAULT_SAMPLE_SIZE,
				DEFAULT_FRAME_RATE,
				DEFAULT_CHUNK_SIZE,
				DEFAULT_MINIMAL_VOLTAGE,
				DEFAULT_MAXIMAL_VOLTAGE);
	}

	/**
	 * @return number of bytes per frame
	 */
	public int frameSize() {
		return channelCount * sampleSize;
	}

	/**
	 * @return frame duration in seconds (s)
	 */
	public double frameDuration() {
		return 1.0 / frameRate;
	}

	/**
	 * @return number of bytes per second (B/s)
	 */
	public int byteRate() {
		return frameRate * frameSize();
	}

	/**
	 * @return Nyquist frequency, half of {@code frame rate}
	 */
	public double nyquistFrequency() {
		return frameRate / 2.0;
	}
}
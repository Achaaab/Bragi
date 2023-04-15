package com.github.achaaab.bragi.common;

import static java.lang.Math.round;

/**
 * @param channelCount
 * @param sampleSize
 * @param frameRate
 * @param chunkDuration duration of chunks in seconds (s)
 * @param minimalVoltage
 * @param maximalVoltage
 * @author Jonathan Guéhenneux
 * @since 0.0.1
 */
public record Settings(

		int channelCount,
		int sampleSize,
		int frameRate,
		float chunkDuration,
		float minimalVoltage,
		float maximalVoltage) {

	public static final Settings INSTANCE = new Settings();

	private static final int DEFAULT_CHANNELS_COUNT = 2;
	private static final int DEFAULT_SAMPLE_SIZE = 2;
	private static final int DEFAULT_FRAME_RATE = 96000;
	private static final float DEFAULT_CHUNK_DURATION = 0.001f;
	private static final float DEFAULT_MINIMAL_VOLTAGE = -5.0f;
	private static final float DEFAULT_MAXIMAL_VOLTAGE = 5.0f;

	/**
	 * Create new default settings.
	 *
	 * @since 0.2.0
	 */
	private Settings() {

		this(
				DEFAULT_CHANNELS_COUNT,
				DEFAULT_SAMPLE_SIZE,
				DEFAULT_FRAME_RATE,
				DEFAULT_CHUNK_DURATION,
				DEFAULT_MINIMAL_VOLTAGE,
				DEFAULT_MAXIMAL_VOLTAGE);
	}

	/**
	 * @return number of samples per chunk
	 * @since 0.2.0
	 */
	public int chunkSize() {
		return round(frameRate * chunkDuration);
	}

	/**
	 * @return number of bytes per frame
	 * @since 0.2.0
	 */
	public int frameSize() {
		return channelCount * sampleSize;
	}

	/**
	 * @return frame duration in seconds (s)
	 * @since 0.2.0
	 */
	public double frameDuration() {
		return 1.0 / frameRate;
	}

	/**
	 * @return number of bytes per second (B/s)
	 * @since 0.2.0
	 */
	public int byteRate() {
		return frameRate * frameSize();
	}

	/**
	 * Nyquist frequency is half of the frame rate. It is the maximum component frequency of the signal.
	 *
	 * @return Nyquist frequency in hertz (Hz)
	 * @since 0.2.0
	 */
	public double nyquistFrequency() {
		return frameRate / 2.0;
	}
}

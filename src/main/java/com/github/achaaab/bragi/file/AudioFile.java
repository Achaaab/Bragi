package com.github.achaaab.bragi.file;

import com.github.achaaab.bragi.common.Normalizer;
import com.github.achaaab.bragi.common.Settings;

/**
 * Contract for audio files.
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.6
 */
public interface AudioFile {

	int ONE_BYTE_MIN_VALUE = 0xFF_FF_FF_80;
	int ONE_BYTE_MAX_VALUE = 0x00_00_00_7F;

	int TWO_BYTES_MIN_VALUE = 0xFF_FF_80_00;
	int TWO_BYTES_MAX_VALUE = 0x00_00_7F_FF;

	int THREE_BYTES_MIN_VALUE = 0xFF_80_00_00;
	int THREE_BYTES_MAX_VALUE = 0x00_7F_FF_FF;

	Normalizer ONE_BYTE_NORMALIZER = new Normalizer(
			ONE_BYTE_MIN_VALUE, ONE_BYTE_MAX_VALUE,
			Settings.INSTANCE.minimalVoltage(), Settings.INSTANCE.maximalVoltage());

	Normalizer TWO_BYTES_NORMALIZER = new Normalizer(
			TWO_BYTES_MIN_VALUE, TWO_BYTES_MAX_VALUE,
			Settings.INSTANCE.minimalVoltage(), Settings.INSTANCE.maximalVoltage());

	Normalizer THREE_BYTES_NORMALIZER = new Normalizer(
			THREE_BYTES_MIN_VALUE, THREE_BYTES_MAX_VALUE,
			Settings.INSTANCE.minimalVoltage(), Settings.INSTANCE.maximalVoltage());

	/**
	 * Opens the audio file.
	 *
	 * @throws AudioFileException exception while opening the audio file
	 * @since 0.2.0
	 */
	void open() throws AudioFileException;

	/**
	 * Closes the audio file.
	 *
	 * @throws AudioFileException exception while opening the audio file
	 * @since 0.2.0
	 */
	void close() throws AudioFileException;

	/**
	 * @return current playback time in seconds (s)
	 * @since 0.2.0
	 */
	float time();

	/**
	 * @return file duration in seconds (s)
	 * @since 0.2.0
	 */
	float duration();

	/**
	 * Seeks the target time in the file.
	 *
	 * @param targetTime time to seek in seconds (s)
	 * @throws AudioFileException exception while seeking the target time
	 * @since 0.2.0
	 */
	void seekTime(double targetTime) throws AudioFileException;

	/**
	 * @return read chunk, {@code null} if the audio file is ended
	 * @throws AudioFileException exception while reading a chunk from this file
	 * @since 0.2.0
	 */
	float[][] readChunk() throws AudioFileException;

	/**
	 * @return sample rate of this file
	 * @since 0.2.0
	 */
	float sampleRate();

	/**
	 * Normalizes an integer sample.
	 *
	 * @param sample an integer sample
	 * @param sampleSize size of the sample in bits (b)
	 * @return normalized float sample between minimal voltage and maximal voltage
	 * @throws AudioFileException if sample size is not supported
	 * @since 0.2.0
	 */
	default float normalize(int sample, int sampleSize) throws AudioFileException {

		return switch (sampleSize) {
			case 8 -> ONE_BYTE_NORMALIZER.normalize(sample);
			case 16 -> TWO_BYTES_NORMALIZER.normalize(sample);
			case 24 -> THREE_BYTES_NORMALIZER.normalize(sample);
			default -> throw new AudioFileException("unsupported sample size: " + sampleSize + " bits");
		};
	}
}

package com.github.achaaab.bragi.file;

/**
 * Contract for audio files.
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.6
 */
public interface AudioFile {

	/**
	 * Opens the audio file.
	 *
	 * @throws AudioFileException exception while opening the audio file
	 */
	void open() throws AudioFileException;

	/**
	 * Closes the audio file.
	 *
	 * @throws AudioFileException exception while opening the audio file
	 */
	void close() throws AudioFileException;

	/**
	 * @return current playback time in seconds (s)
	 */
	float getTime();

	/**
	 * @return file duration in seconds (s)
	 */
	float getDuration();

	/**
	 * Seeks the target time in the file.
	 *
	 * @param targetTime time to seek in seconds (s)
	 * @throws AudioFileException exception while seeking the target time
	 */
	void seekTime(double targetTime) throws AudioFileException;

	/**
	 * @return read chunk, {@code null} if the audio file is ended
	 * @throws AudioFileException exception while reading a chunk from this file
	 */
	float[][] readChunk() throws AudioFileException;
}
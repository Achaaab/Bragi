package com.github.achaaab.bragi.file;

/**
 * WAV file header
 *
 * @param fileType
 * @param fileSize
 * @param fileFormat
 * @param formatChunkTitle
 * @param formatChunkSize
 * @param audioFormat
 * @param channelCount
 * @param frameRate
 * @param byteRate
 * @param frameSize
 * @param sampleSize
 * @param dataChunkTitle
 * @param dataSize
 * @author Jonathan Guéhenneux
 * @since 0.1.0
 */
public record WavFileHeader(

		String fileType,
		int fileSize,
		String fileFormat,
		String formatChunkTitle,
		int formatChunkSize,
		short audioFormat,
		short channelCount,
		int frameRate,
		int byteRate,
		short frameSize,
		short sampleSize,
		String dataChunkTitle,
		int dataSize) {

}

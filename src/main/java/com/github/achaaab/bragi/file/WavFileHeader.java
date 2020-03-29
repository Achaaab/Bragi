package com.github.achaaab.bragi.file;

/**
 * WAV file header
 * TODO add javadoc about this record components
 *
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
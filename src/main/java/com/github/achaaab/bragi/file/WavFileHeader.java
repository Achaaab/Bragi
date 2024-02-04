package com.github.achaaab.bragi.file;

/**
 * WAV file header
 *
 * @param fileType file type, should be {@link #FILE_TYPE}
 * @param fileSize file size minus 8 (in bytes)
 * @param fileFormat file format, should be {@link #FILE_FORMAT}
 * @param formatChunkTitle format chunk title, should be {@link #FORMAT_CHUNK_TITLE}
 * @param formatChunkSize format chunk size (in bytes)
 * @param formatCode format code, only {@link #FORMAT_PCM} is supported
 * @param channelCount number of channels
 * @param frameRate frame rate in frames per second
 * @param byteRate byte rate in byte per second
 * @param frameSize frame size in bytes
 * @param sampleSize sample size in bytes
 * @param dataChunkTitle data chunk title, should be {@link #DATA_CHUNK_TITLE}
 * @param dataSize data size (in bytes)
 * @author Jonathan Guéhenneux
 * @since 0.1.0
 */
public record WavFileHeader(

		String fileType,
		int fileSize,
		String fileFormat,
		String formatChunkTitle,
		int formatChunkSize,
		short formatCode,
		short channelCount,
		int frameRate,
		int byteRate,
		short frameSize,
		short sampleSize,
		String dataChunkTitle,
		int dataSize) {

	public static final String FILE_TYPE = "RIFF";
	public static final String FILE_FORMAT = "WAVE";
	public static final String FORMAT_CHUNK_TITLE = "fmt ";

	/**
	 * (Pulse Code Modulation) = linear quantization
	 */
	public static final short FORMAT_PCM = 1;

	public static final String DATA_CHUNK_TITLE = "data";
}

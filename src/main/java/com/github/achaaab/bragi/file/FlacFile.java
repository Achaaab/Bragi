package com.github.achaaab.bragi.file;

import com.github.achaaab.bragi.codec.flac.FlacDecoderException;
import com.github.achaaab.bragi.codec.flac.FlacHeader;
import com.github.achaaab.bragi.codec.flac.FlacInputStream;
import org.slf4j.Logger;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.file.Path;

import static com.github.achaaab.bragi.codec.flac.FlacDecoder.decodeFrame;
import static com.github.achaaab.bragi.codec.flac.FlacDecoder.decodeHeader;
import static java.nio.file.Files.newInputStream;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Jonathan Guéhenneux
 * @since 0.1.7
 */
public class FlacFile implements AudioFile {

	private static final Logger LOGGER = getLogger(FlacFile.class);

	private final Path path;

	private FlacInputStream flacInputStream;
	private FlacHeader header;
	private float time;

	/**
	 * @param path path to a FLAC file
	 */
	public FlacFile(Path path) {
		this.path = path;
	}

	@Override
	public void open() throws AudioFileException {

		LOGGER.info("opening FLAC file {}", path);

		try {

			var inputStream = newInputStream(path);
			var bufferedInputStream = new BufferedInputStream(inputStream);
			flacInputStream = new FlacInputStream(bufferedInputStream);
			header = decodeHeader(flacInputStream);
			time = 0.0f;

		} catch (IOException | FlacDecoderException cause) {

			throw new AudioFileException(cause);
		}
	}

	@Override
	public void close() throws AudioFileException {

		LOGGER.info("closing FLAC file {}", path);

		try {
			flacInputStream.close();
		} catch (IOException cause) {
			throw new AudioFileException(cause);
		}
	}

	@Override
	public float getTime() {
		return time;
	}

	@Override
	public float getDuration() {
		return header.streamInfo().duration();
	}

	@Override
	public void seekTime(double targetTime) {

	}

	@Override
	public float[][] readChunk() throws AudioFileException {

		float[][] chunk = null;

		try {

			var frame = decodeFrame(flacInputStream, header.streamInfo());

			if (frame != null) {

				var channelCount = frame.length;
				var sampleCount = frame[0].length;

				time += (double) sampleCount / header.streamInfo().sampleRate();

				chunk = new float[channelCount][sampleCount];

				for (var channelIndex = 0; channelIndex < channelCount; channelIndex++) {

					for (var sampleIndex = 0; sampleIndex < sampleCount; sampleIndex++) {
						chunk[channelIndex][sampleIndex] = normalize(frame[channelIndex][sampleIndex]);
					}
				}
			}

		} catch (IOException | FlacDecoderException cause) {

			throw new AudioFileException(cause);
		}

		return chunk;
	}

	@Override
	public float getSampleRate() {
		return header.streamInfo().sampleRate();
	}

	@Override
	public int sampleSize() {
		return header.streamInfo().sampleSize();
	}
}
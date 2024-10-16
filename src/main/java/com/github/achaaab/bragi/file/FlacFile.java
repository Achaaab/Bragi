package com.github.achaaab.bragi.file;

import com.github.achaaab.bragi.codec.flac.FlacException;
import com.github.achaaab.bragi.codec.flac.FlacHeader;
import com.github.achaaab.bragi.codec.flac.FlacInputStream;
import com.github.achaaab.bragi.codec.flac.frame.Frame;
import org.slf4j.Logger;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.file.Path;

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
	 * @since 0.2.0
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
			header = new FlacHeader(flacInputStream);
			time = 0.0f;

		} catch (IOException | FlacException cause) {

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
	public float time() {
		return time;
	}

	@Override
	public float duration() {
		return header.streamInfo().duration();
	}

	@Override
	public void seekTime(double targetTime) {

	}

	@Override
	public float[][] readChunk() throws AudioFileException {

		float[][] chunk;

		try {

			var frame = new Frame(flacInputStream, header.streamInfo());
			var frameHeader = frame.header();
			var channelCount = frameHeader.channelAssignment().channelCount();
			var sampleCount = frameHeader.blockSize();
			var sampleSize = frameHeader.sampleSize();
			var samples = frame.samples();

			time += (float) sampleCount / header.streamInfo().sampleRate();

			chunk = new float[channelCount][sampleCount];

			for (var channelIndex = 0; channelIndex < channelCount; channelIndex++) {

				for (var sampleIndex = 0; sampleIndex < sampleCount; sampleIndex++) {

					var sample = samples[channelIndex][sampleIndex];
					chunk[channelIndex][sampleIndex] = normalize(sample, sampleSize);
				}
			}

		} catch (EOFException endOfFile) {

			chunk = null;

		} catch (IOException | FlacException cause) {

			throw new AudioFileException(cause);
		}

		return chunk;
	}

	@Override
	public float sampleRate() {
		return header.streamInfo().sampleRate();
	}
}

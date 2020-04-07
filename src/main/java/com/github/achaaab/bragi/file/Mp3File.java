package com.github.achaaab.bragi.file;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.DecoderException;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.SampleBuffer;
import org.slf4j.Logger;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.file.Path;

import static java.nio.file.Files.newInputStream;
import static java.nio.file.Files.size;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * An MP3 file is basically made of frames.
 * <p>
 * Those MP3 frames are different from frames in Bragi: they are chunks of samples
 * (sometimes 384, sometimes 1152...).
 * <p>
 * Each frame is preceded by a header with useful information like channel count and sample frequency.
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.4
 */
public class Mp3File implements AudioFile {

	private static final Logger LOGGER = getLogger(Mp3File.class);

	private static final int SAMPLE_SIZE = 16;

	private final Path path;

	private Bitstream bitStream;
	private Decoder decoder;
	private int sampleRate;
	private float time;
	private float duration;
	private boolean ended;

	/**
	 * @param path path to MP3 file
	 */
	public Mp3File(Path path) {
		this.path = path;
	}

	/**
	 * @return time of the current MP3 frame in seconds
	 */
	public float getTime() {
		return time;
	}

	/**
	 * @return estimated duration of this MP3 file in seconds
	 */
	public float getDuration() {
		return duration;
	}

	/**
	 * Seeks the first MP3 frame whose time is greater than target time.
	 *
	 * @param targetTime target time in seconds
	 * @throws AudioFileException exception while seeking the target frame
	 */
	public void seekTime(double targetTime) throws AudioFileException {

		if (targetTime < time) {

			close();
			open();
		}

		while (!ended && time < targetTime) {
			skipFrame();
		}
	}


	/**
	 * Opens the MP3 file.
	 *
	 * @throws AudioFileException exception while opening the MP3 file
	 */
	public void open() throws AudioFileException {

		try {

			LOGGER.info("opening MP3 file {}", path);

			var stream = newInputStream(path);
			var bufferedStream = new BufferedInputStream(stream);
			bitStream = new Bitstream(bufferedStream);
			decoder = new Decoder();

			time = 0.0f;
			duration = 0.0f;
			ended = false;

		} catch (IOException cause) {

			throw new AudioFileException(cause);
		}
	}

	/**
	 * Closes the MP3 file.
	 *
	 * @throws AudioFileException exception while closing the MP3 file
	 */
	public void close() throws AudioFileException {

		try {

			LOGGER.info("closing MP3 file {}", path);
			bitStream.close();

		} catch (BitstreamException cause) {

			throw new AudioFileException(cause);
		}
	}

	@Override
	public float[][] readChunk() throws AudioFileException {

		float[][] chunk = null;

		var mp3Frame = readFrame();

		if (mp3Frame != null) {

			var channelCount = mp3Frame.getChannelCount();
			var sampleCount = mp3Frame.getBufferLength();
			var frameCount = sampleCount / channelCount;

			chunk = new float[channelCount][frameCount];

			var buffer = mp3Frame.getBuffer();
			var sampleIndex = 0;

			for (var frameIndex = 0; frameIndex < frameCount; frameIndex++) {

				for (var channelIndex = 0; channelIndex < channelCount; channelIndex++) {

					var sample = buffer[sampleIndex++];
					chunk[channelIndex][frameIndex] = TWO_BYTES_NORMALIZER.normalize(sample);
				}
			}
		}

		return chunk;
	}

	@Override
	public float getSampleRate() {
		return sampleRate;
	}

	@Override
	public int sampleSize() {
		return SAMPLE_SIZE;
	}

	/**
	 * Reads the next MP3 frame from this file.
	 *
	 * @return read MP3 frame, or {@code null} if this MP3 file ended
	 * @throws AudioFileException exception while reading the next frame
	 */
	private SampleBuffer readFrame() throws AudioFileException {

		try {

			SampleBuffer frame;
			var header = bitStream.readFrame();

			if (header == null) {

				ended = true;
				frame = null;

			} else {

				addFrameDuration(header);

				frame = (SampleBuffer) decoder.decodeFrame(header, bitStream);
				sampleRate = frame.getSampleFrequency();
				bitStream.closeFrame();
			}

			return frame;

		} catch (BitstreamException | DecoderException | IOException cause) {

			throw new AudioFileException(cause);
		}
	}

	/**
	 * Skips the current frame.
	 *
	 * @throws AudioFileException exception while skipping the frame
	 */
	private void skipFrame() throws AudioFileException {

		try {

			var header = bitStream.readFrame();

			if (header == null) {

				ended = true;

			} else {

				addFrameDuration(header);
				bitStream.closeFrame();
			}

		} catch (BitstreamException | IOException cause) {

			throw new AudioFileException(cause);
		}
	}

	/**
	 * Adds the duration of the frame to the current time. If it's the first frame, also estimate the total duration
	 * of this MP3 file.
	 *
	 * @param header header of the frame
	 * @throws IOException I/O exception while estimating the tot
	 */
	private void addFrameDuration(Header header) throws IOException {

		time += header.ms_per_frame() / 1_000;

		if (duration == 0.0f) {

			var longSize = size(path);
			var size = Integer.MAX_VALUE;

			if (longSize > size) {

				var byteDuration = header.total_ms(size) / size;
				duration = longSize * byteDuration / 1000;

			} else {

				size = (int) longSize;
				duration = header.total_ms(size) / 1000;
			}
		}
	}
}
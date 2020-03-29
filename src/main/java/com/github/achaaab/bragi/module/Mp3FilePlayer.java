package com.github.achaaab.bragi.module;

import com.github.achaaab.bragi.common.ModuleCreationException;
import com.github.achaaab.bragi.common.Normalizer;
import com.github.achaaab.bragi.common.Settings;
import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.DecoderException;
import javazoom.jl.decoder.SampleBuffer;
import org.slf4j.Logger;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.file.Path;

import static java.nio.file.Files.newInputStream;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * MP3 file player
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.4
 */
public class Mp3FilePlayer extends Module implements Player {

	private static final Logger LOGGER = getLogger(Mp3FilePlayer.class);

	public static final String DEFAULT_NAME = "mp3_player";

	private static final Normalizer NORMALIZER = new Normalizer(
			Short.MIN_VALUE, Short.MAX_VALUE,
			Settings.INSTANCE.getMinimalVoltage(), Settings.INSTANCE.getMaximalVoltage()
	);

	private Bitstream bitStream;
	private Decoder decoder;
	private int channelCount;

	/**
	 * Creates a MP3 file player with default name.
	 *
	 * @param path path to the MP3 file to play
	 * @throws ModuleCreationException if the MP3 file cannot be played
	 * @see #DEFAULT_NAME
	 * @since 0.0.9
	 */
	public Mp3FilePlayer(Path path) {
		this(DEFAULT_NAME, path);
	}

	/**
	 * @param name name of the MP3 file player
	 * @param path path to the MP3 file to play
	 * @throws ModuleCreationException if the MP3 file cannot be played
	 */
	public Mp3FilePlayer(String name, Path path) {

		super(name);

		addPrimaryOutput(name + "_output_" + outputs.size());

		while (outputs.size() < Settings.INSTANCE.getChannelCount()) {
			addSecondaryOutput(name + "_output_" + outputs.size());
		}

		try {

			var inputStream = new BufferedInputStream(newInputStream(path));
			bitStream = new Bitstream(inputStream);

		} catch (IOException cause) {

			throw new ModuleCreationException(cause);
		}

		decoder = new Decoder();

		start();
	}

	@Override
	public int compute() throws InterruptedException {

		try {

			var header = bitStream.readFrame();
			var frameCount = 0;

			if (header != null) {

				var sampleBuffer = (SampleBuffer) decoder.decodeFrame(header, bitStream);

				bitStream.closeFrame();

				var samples = convert(sampleBuffer);

				for (var channelIndex = 0; channelIndex < channelCount; channelIndex++) {
					outputs.get(channelIndex).write(samples[channelIndex]);
				}

				frameCount = samples[0].length;
			}

			return frameCount;

		} catch (BitstreamException | DecoderException cause) {

			throw new RuntimeException(cause);
		}
	}

	/**
	 * Convert an MP3 sample buffer to normalized {@code float} samples, split by channel.
	 *
	 * @param sampleBuffer MP3 sample buffer to split in channels
	 */
	private float[][] convert(SampleBuffer sampleBuffer) {

		channelCount = sampleBuffer.getChannelCount();

		var frameCount = sampleBuffer.getBufferLength() / channelCount;
		var buffer = sampleBuffer.getBuffer();
		var samples = new float[channelCount][frameCount];

		var sampleIndex = 0;

		for (int frameIndex = 0; frameIndex < frameCount; frameIndex++) {

			for (int channelIndex = 0; channelIndex < channelCount; channelIndex++) {
				samples[channelIndex][frameIndex] = NORMALIZER.normalize(buffer[sampleIndex++]);
			}
		}

		return samples;
	}

	@Override
	public void pause() {

	}

	@Override
	public void play() {

	}

	@Override
	public void setTime(double time) {

	}

	@Override
	public void stop() {

	}
}
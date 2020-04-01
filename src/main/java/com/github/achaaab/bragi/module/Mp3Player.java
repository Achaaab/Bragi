package com.github.achaaab.bragi.module;

import com.github.achaaab.bragi.common.ModuleCreationException;
import com.github.achaaab.bragi.common.ModuleExecutionException;
import com.github.achaaab.bragi.common.Normalizer;
import com.github.achaaab.bragi.common.Settings;
import com.github.achaaab.bragi.file.Mp3File;
import com.github.achaaab.bragi.file.Mp3FileException;
import javazoom.jl.decoder.SampleBuffer;
import org.slf4j.Logger;

import java.nio.file.Path;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * MP3 player
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.4
 */
public class Mp3Player extends Player {

	private static final Logger LOGGER = getLogger(Mp3Player.class);

	public static final String DEFAULT_NAME = "mp3_player";

	private static final Normalizer NORMALIZER = new Normalizer(
			Short.MIN_VALUE, Short.MAX_VALUE,
			Settings.INSTANCE.minimalVoltage(), Settings.INSTANCE.maximalVoltage()
	);

	private Mp3File file;

	/**
	 * Creates a MP3 file player with default name.
	 *
	 * @param path path to the MP3 file to play
	 * @throws ModuleCreationException if the MP3 file cannot be played
	 * @see #DEFAULT_NAME
	 * @since 0.0.9
	 */
	public Mp3Player(Path path) {
		this(DEFAULT_NAME, path);
	}

	/**
	 * @param name name of the MP3 file player
	 * @param path path to the MP3 file to play
	 * @throws ModuleCreationException if the MP3 file cannot be played
	 */
	public Mp3Player(String name, Path path) {

		super(name);

		addPrimaryOutput(name + "_output_" + outputs.size());

		while (outputs.size() < Settings.INSTANCE.channelCount()) {
			addSecondaryOutput(name + "_output_" + outputs.size());
		}

		try {
			file = new Mp3File(path);
		} catch (Mp3FileException cause) {
			throw new ModuleCreationException(cause);
		}

		start();
	}

	@Override
	public void stop() {

		synchronized (file) {

			try {
				file.reopen();
			} catch (Mp3FileException cause) {
				throw new ModuleExecutionException(cause);
			}
		}

		super.stop();
	}

	@Override
	public void seek(double time) {

		synchronized (file) {

			try {
				file.seekTime(time);
			} catch (Mp3FileException cause) {
				throw new ModuleCreationException(cause);
			}
		}

		super.seek(file.getTime());
	}

	@Override
	public int playChunk() throws InterruptedException {

		try {

			var frameCount = 0;
			SampleBuffer mp3Frame;

			synchronized (file) {
				mp3Frame = file.readFrame();
			}

			if (mp3Frame == null) {

				stop();

			} else {

				setTime(file.getTime());

				var channelCount = mp3Frame.getChannelCount();
				var sampleCount = mp3Frame.getBufferLength();
				frameCount = sampleCount / channelCount;

				var buffer = mp3Frame.getBuffer();
				var sampleIndex = 0;

				var samples = new float[channelCount][frameCount];

				for (var frameIndex = 0; frameIndex < frameCount; frameIndex++) {

					for (var channelIndex = 0; channelIndex < channelCount; channelIndex++) {

						var sample = buffer[sampleIndex++];
						samples[channelIndex][frameIndex] = NORMALIZER.normalize(sample);
					}
				}

				for (var channelIndex = 0; channelIndex < channelCount; channelIndex++) {
					outputs.get(channelIndex).write(samples[channelIndex]);
				}
			}

			return frameCount;

		} catch (Mp3FileException cause) {

			throw new ModuleExecutionException(cause);
		}
	}
}
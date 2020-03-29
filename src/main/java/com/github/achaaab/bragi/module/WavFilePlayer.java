package com.github.achaaab.bragi.module;

import com.github.achaaab.bragi.common.MalformedWavFileException;
import com.github.achaaab.bragi.common.ModuleCreationException;
import com.github.achaaab.bragi.common.Settings;
import com.github.achaaab.bragi.common.WavFile;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * WAV file player
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.9
 */
public class WavFilePlayer extends Module implements Player {

	private static final Logger LOGGER = getLogger(WavFilePlayer.class);

	public static final String DEFAULT_NAME = "wav_file_player";

	private WavFile wavFile;
	private int offset;
	private int channelCount;

	/**
	 * Creates a WAV file player with default name.
	 *
	 * @param file WAV file to play
	 * @throws ModuleCreationException if the WAV file cannot be played
	 * @see #DEFAULT_NAME
	 * @since 0.0.9
	 */
	public WavFilePlayer(File file) {
		this(DEFAULT_NAME, file);
	}

	/**
	 * @param name name of the WAV file player to create
	 * @param file WAV file to play
	 * @throws ModuleCreationException if the WAV file cannot be played
	 */
	public WavFilePlayer(String name, File file) {

		super(name);

		addPrimaryOutput(name + "_output_" + outputs.size());

		while (outputs.size() < Settings.INSTANCE.getChannelCount()) {
			addSecondaryOutput(name + "_output_" + outputs.size());
		}

		try {
			wavFile = new WavFile(file);
		} catch (IOException | MalformedWavFileException cause) {
			throw new ModuleCreationException(cause);
		}

		channelCount = wavFile.getChannelCount();
		offset = 0;

		start();
	}

	@Override
	public int compute() throws InterruptedException {

		var bufferSizeInFrames = Settings.INSTANCE.getChunkSize();
		var samples = new float[channelCount][bufferSizeInFrames];

		try {

			var count = wavFile.read(samples, offset);

			if (count <= 0) {
				offset = 0;
			} else {
				offset += count;
			}

		} catch (IOException cause) {

			throw new RuntimeException(cause);
		}

		for (var channelIndex = 0; channelIndex < channelCount; channelIndex++) {
			outputs.get(channelIndex).write(samples[channelIndex]);
		}

		return bufferSizeInFrames;
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
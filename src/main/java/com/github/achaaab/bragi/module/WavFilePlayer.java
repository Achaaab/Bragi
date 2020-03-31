package com.github.achaaab.bragi.module;

import com.github.achaaab.bragi.common.ModuleCreationException;
import com.github.achaaab.bragi.common.ModuleExecutionException;
import com.github.achaaab.bragi.common.Settings;
import com.github.achaaab.bragi.file.WavFile;
import com.github.achaaab.bragi.file.WavFileException;
import org.slf4j.Logger;

import java.io.File;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * WAV file player
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.9
 */
public class WavFilePlayer extends Player {

	private static final Logger LOGGER = getLogger(WavFilePlayer.class);

	public static final String DEFAULT_NAME = "wav_file_player";

	private final WavFile wavFile;
	private final int channelCount;

	private int offset;

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

		while (outputs.size() < Settings.INSTANCE.channelCount()) {
			addSecondaryOutput(name + "_output_" + outputs.size());
		}

		try {
			wavFile = new WavFile(file);
		} catch (WavFileException cause) {
			throw new ModuleCreationException(cause);
		}

		channelCount = wavFile.getHeader().channelCount();
		offset = 0;

		start();
	}

	@Override
	public int playChunk() throws InterruptedException {

		var bufferSizeInFrames = Settings.INSTANCE.chunkSize();
		var samples = new float[channelCount][bufferSizeInFrames];

		try {

			var count = wavFile.read(samples, offset);

			if (count <= 0) {
				offset = 0;
			} else {
				offset += count;
			}

		} catch (WavFileException cause) {

			throw new ModuleExecutionException(cause);
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
	public void seek(double time) {

	}

	@Override
	public void stop() {

	}
}
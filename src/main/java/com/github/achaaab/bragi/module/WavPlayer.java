package com.github.achaaab.bragi.module;

import com.github.achaaab.bragi.common.ModuleCreationException;
import com.github.achaaab.bragi.common.ModuleExecutionException;
import com.github.achaaab.bragi.common.Settings;
import com.github.achaaab.bragi.file.WavFile;
import com.github.achaaab.bragi.file.WavFileException;
import org.slf4j.Logger;

import java.nio.file.Path;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * WAV file player
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.9
 */
public class WavPlayer extends Player {

	private static final Logger LOGGER = getLogger(WavPlayer.class);

	public static final String DEFAULT_NAME = "wav_player";

	private final WavFile file;
	private final int channelCount;

	private int offset;

	/**
	 * Creates a WAV player with default name.
	 *
	 * @param path path to the WAV file to play
	 * @throws ModuleCreationException if the WAV file cannot be played
	 * @see #DEFAULT_NAME
	 * @since 0.0.9
	 */
	public WavPlayer(Path path) {
		this(DEFAULT_NAME, path);
	}

	/**
	 * @param name name of the WAV player to create
	 * @param path path to the WAV file to play
	 * @throws ModuleCreationException if the WAV file cannot be played
	 */
	public WavPlayer(String name, Path path) {

		super(name);

		addPrimaryOutput(name + "_output_" + outputs.size());

		while (outputs.size() < Settings.INSTANCE.channelCount()) {
			addSecondaryOutput(name + "_output_" + outputs.size());
		}

		try {
			this.file = new WavFile(path.toFile());
		} catch (WavFileException cause) {
			throw new ModuleCreationException(cause);
		}

		LOGGER.info("header: " + this.file.getHeader());

		channelCount = this.file.getHeader().channelCount();
		offset = 0;

		start();
	}

	@Override
	public int playChunk() throws InterruptedException {

		var bufferSizeInFrames = Settings.INSTANCE.chunkSize();
		var samples = new float[channelCount][bufferSizeInFrames];

		try {

			var count = file.read(samples, offset);

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
}
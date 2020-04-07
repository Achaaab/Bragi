package com.github.achaaab.bragi.core.module.player;

import com.github.achaaab.bragi.core.module.ModuleCreationException;
import com.github.achaaab.bragi.file.WavFile;

import java.nio.file.Path;

/**
 * WAV file player
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.9
 */
public class WavPlayer extends Player {

	public static final String DEFAULT_NAME = "wav_player";

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
		super(name, new WavFile(path));
	}
}
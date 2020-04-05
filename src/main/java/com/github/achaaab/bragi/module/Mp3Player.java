package com.github.achaaab.bragi.module;

import com.github.achaaab.bragi.common.ModuleCreationException;
import com.github.achaaab.bragi.file.Mp3File;

import java.nio.file.Path;

/**
 * MP3 player
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.4
 */
public class Mp3Player extends Player {

	public static final String DEFAULT_NAME = "mp3_player";

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
		super(name, new Mp3File(path));
	}
}
package com.github.achaaab.bragi.module.player;

import com.github.achaaab.bragi.common.ModuleCreationException;
import com.github.achaaab.bragi.file.FlacFile;

import java.nio.file.Path;

/**
 * FLAC player
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.7
 */
public class FlacPlayer extends Player {

	public static final String DEFAULT_NAME = "flac_player";

	/**
	 * Creates a FLAC file player with default name.
	 *
	 * @param path path to the FLAC file to play
	 * @throws ModuleCreationException if the FLAC file cannot be played
	 * @see #DEFAULT_NAME
	 */
	public FlacPlayer(Path path) {
		this(DEFAULT_NAME, path);
	}

	/**
	 * @param name name of the FLAC file player
	 * @param path path to the FLAC file to play
	 * @throws ModuleCreationException if the FLAC file cannot be played
	 */
	public FlacPlayer(String name, Path path) {
		super(name, new FlacFile(path));
	}
}
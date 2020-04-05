package com.github.achaaab.bragi.file;

/**
 * Encapsulates any kind of exception while, opening, reading or closing an audio file.
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.6
 */
public class AudioFileException extends Exception {

	/**
	 * @param message exception message
	 */
	public AudioFileException(String message) {
		super(message);
	}

	/**
	 * @param cause cause of this exception
	 */
	public AudioFileException(Exception cause) {
		super(cause);
	}
}
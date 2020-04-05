package com.github.achaaab.bragi.file;

/**
 * @author Jonathan Guéhenneux
 * @since 0.1.0
 */
public class WavFileException extends AudioFileException {

	/**
	 * @param message exception message
	 */
	public WavFileException(String message) {
		super(message);
	}

	/**
	 * @param cause cause of this exception
	 * @since 0.1.4
	 */
	public WavFileException(Exception cause) {
		super(cause);
	}
}
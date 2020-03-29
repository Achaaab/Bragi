package com.github.achaaab.bragi.file;

/**
 * @author Jonathan Guéhenneux
 * @since 0.1.0
 */
public class MalformedWavFileException extends Exception {

	/**
	 * @param message exception message
	 */
	public MalformedWavFileException(String message) {
		super(message);
	}
}
package com.github.achaaab.bragi.mml;

/**
 * Exception thrown during MML playing.
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.8
 */
public class MmlException extends RuntimeException {

	private static final String DEFAULT_MESSAGE = "An exception occurred while playing MML.";

	/**
	 * @param cause cause of MML playing failure
	 * @since 0.2.0
	 */
	public MmlException(Exception cause) {
		super(DEFAULT_MESSAGE, cause);
	}

	/**
	 * @param message message explaining the MML exception to create
	 * @since 0.2.0
	 */
	public MmlException(String message) {
		super(message);
	}
}

package com.github.achaaab.bragi.core;

/**
 * @author Jonathan Guéhenneux
 * @since 0.1.8
 */
public class SynthesizerCreationException extends RuntimeException {

	private static final String DEFAULT_MESSAGE = "An exception prevented the creation of a synthesizer.";

	/**
	 * @param cause cause of synthesizer creation failure
	 */
	public SynthesizerCreationException(Exception cause) {
		super(DEFAULT_MESSAGE, cause);
	}
}
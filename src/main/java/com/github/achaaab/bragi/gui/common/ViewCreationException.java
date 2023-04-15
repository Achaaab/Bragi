package com.github.achaaab.bragi.gui.common;

/**
 * @author Jonathan Guéhenneux
 * @since 0.1.6
 */
public class ViewCreationException extends RuntimeException {

	/**
	 * Creates a runtime exception from the exception that prevented the view creation.
	 *
	 * @param cause exception that prevented the view creation
	 * @since 0.2.0
	 */
	public ViewCreationException(Exception cause) {
		super(cause);
	}
}

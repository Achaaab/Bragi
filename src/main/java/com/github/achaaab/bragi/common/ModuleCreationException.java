package com.github.achaaab.bragi.common;

/**
 * Exception thrown by {@link com.github.achaaab.bragi.module.Module} constructors.
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.9
 */
public class ModuleCreationException extends RuntimeException {

	private static final String DEFAULT_MESSAGE = "an exception prevented module creation";

	/**
	 * @param cause cause of module creation failure
	 */
	public ModuleCreationException(Exception cause) {
		super(DEFAULT_MESSAGE, cause);
	}
}
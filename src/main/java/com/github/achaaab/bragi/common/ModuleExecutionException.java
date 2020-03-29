package com.github.achaaab.bragi.common;

/**
 * Exception thrown during {@link com.github.achaaab.bragi.module.Module} execution.
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.0
 */
public class ModuleExecutionException extends RuntimeException {

	private static final String DEFAULT_MESSAGE = "an exception prevented module execution";

	/**
	 * @param cause cause of module execution failure
	 */
	public ModuleExecutionException(Exception cause) {
		super(DEFAULT_MESSAGE, cause);
	}
}
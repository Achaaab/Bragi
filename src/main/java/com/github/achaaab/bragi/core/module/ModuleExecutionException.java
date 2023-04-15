package com.github.achaaab.bragi.core.module;

/**
 * Exception thrown during {@link com.github.achaaab.bragi.core.module.Module} execution.
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.0
 */
public class ModuleExecutionException extends RuntimeException {

	private static final String DEFAULT_MESSAGE = "An exception prevented the execution of a module.";

	/**
	 * @param cause cause of module execution failure
	 * @since 0.2.0
	 */
	public ModuleExecutionException(Exception cause) {
		super(DEFAULT_MESSAGE, cause);
	}
}

package com.github.achaaab.bragi.core.module;

/**
 * Exception thrown by {@link com.github.achaaab.bragi.core.module.Module} constructors.
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.9
 */
public class ModuleCreationException extends RuntimeException {

	private static final String DEFAULT_MESSAGE = "An exception prevented the creation of a module.";

	/**
	 * @param cause cause of module creation failure
	 */
	public ModuleCreationException(Exception cause) {
		super(DEFAULT_MESSAGE, cause);
	}
}
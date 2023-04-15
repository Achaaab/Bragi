package com.github.achaaab.bragi.core.configuration;

/**
 * exception while configuring a synthesizer
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.8
 */
public class ConfigurationException extends RuntimeException {

	private static final String DEFAULT_MESSAGE = "An exception prevented the configuration.";

	/**
	 * @param message message that explains the configuration failure
	 * @since 0.2.0
	 */
	public ConfigurationException(String message) {
		super(message);
	}

	/**
	 * @param cause cause of configuration failure
	 * @since 0.2.0
	 */
	public ConfigurationException(Exception cause) {
		super(DEFAULT_MESSAGE, cause);
	}
}

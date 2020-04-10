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
	 * @param cause cause of configuration failure
	 */
	public ConfigurationException(Exception cause) {
		super(DEFAULT_MESSAGE, cause);
	}
}
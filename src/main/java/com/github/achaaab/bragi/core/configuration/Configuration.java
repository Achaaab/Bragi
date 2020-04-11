package com.github.achaaab.bragi.core.configuration;

import javax.sound.sampled.Mixer;

/**
 * @author Jonathan Guéhenneux
 * @since 0.1.8
 */
public class Configuration {

	private final LineConfiguration inputConfiguration;
	private final LineConfiguration outputConfiguration;

	/**
	 * Creates a new synthesizer.
	 */
	public Configuration() {

		inputConfiguration = new LineConfiguration(Mixer::getTargetLineInfo);
		outputConfiguration = new LineConfiguration(Mixer::getSourceLineInfo);
	}

	/**
	 * @return configuration of the input line
	 */
	public LineConfiguration getInputConfiguration() {
		return inputConfiguration.copy();
	}

	/**
	 * @param inputConfiguration configuration of the input line to set
	 */
	public void setInputConfiguration(LineConfiguration inputConfiguration) {
		this.inputConfiguration.copy(inputConfiguration);
	}

	/**
	 * @return configuration of the output line
	 */
	public LineConfiguration getOutputConfiguration() {
		return outputConfiguration.copy();
	}

	/**
	 * @param outputConfiguration configuration of the output line to set
	 */
	public void setOutputConfiguration(LineConfiguration outputConfiguration) {
		this.outputConfiguration.copy(outputConfiguration);
	}
}
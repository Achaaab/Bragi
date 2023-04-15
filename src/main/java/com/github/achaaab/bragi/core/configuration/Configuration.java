package com.github.achaaab.bragi.core.configuration;

import com.github.achaaab.bragi.core.Synthesizer;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

import static java.lang.Math.round;
import static javax.sound.sampled.AudioSystem.getSourceDataLine;
import static javax.sound.sampled.AudioSystem.getTargetDataLine;

/**
 * synthesizer configuration
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.8
 */
public class Configuration {

	public static final float LINE_BUFFER_DURATION = 0.01F;

	private final Synthesizer synthesizer;

	private final LineConfiguration inputConfiguration;
	private final LineConfiguration outputConfiguration;

	/**
	 * Creates a new configuration for the given synthesizer.
	 *
	 * @param synthesizer synthesizer on which to apply the created configuration
	 * @since 0.2.0
	 */
	public Configuration(Synthesizer synthesizer) {

		this.synthesizer = synthesizer;

		inputConfiguration = new LineConfiguration(Mixer::getTargetLineInfo);
		outputConfiguration = new LineConfiguration(Mixer::getSourceLineInfo);
	}

	/**
	 * @return configuration of the input line
	 * @since 0.2.0
	 */
	public LineConfiguration getInputConfiguration() {
		return inputConfiguration.copy();
	}

	/**
	 * @param inputConfiguration configuration of the input line to set
	 * @since 0.2.0
	 */
	public void setInputConfiguration(LineConfiguration inputConfiguration) {

		this.inputConfiguration.copy(inputConfiguration);

		synthesizer.configure();
	}

	/**
	 * @return configuration of the output line
	 * @since 0.2.0
	 */
	public LineConfiguration getOutputConfiguration() {
		return outputConfiguration.copy();
	}

	/**
	 * @param outputConfiguration configuration of the output line to set
	 * @since 0.2.0
	 */
	public void setOutputConfiguration(LineConfiguration outputConfiguration) {

		this.outputConfiguration.copy(outputConfiguration);

		synthesizer.configure();
	}

	/**
	 * @return configured input line
	 * @since 0.2.0
	 */
	public TargetDataLine inputLine() {

		var mixer = inputConfiguration.getMixer();
		var format = inputConfiguration.format();

		try {

			var line = getTargetDataLine(format, mixer.getMixerInfo());
			var frameSize = format.getFrameSize();
			var sampleRate = format.getSampleRate();
			var byteRate = frameSize * sampleRate;
			var bufferLength = round(byteRate * LINE_BUFFER_DURATION);
			line.open(format, bufferLength);

			return line;

		} catch (LineUnavailableException cause) {

			throw new ConfigurationException(cause);
		}
	}

	/**
	 * @return configured output line
	 * @since 0.2.0
	 */
	public SourceDataLine outputLine() {

		var mixer = outputConfiguration.getMixer();
		var format = outputConfiguration.format();

		try {

			var line = getSourceDataLine(format, mixer.getMixerInfo());
			var frameSize = format.getFrameSize();
			var sampleRate = format.getSampleRate();
			var byteRate = frameSize * sampleRate;
			var bufferLength = round(byteRate * LINE_BUFFER_DURATION);
			line.open(format, bufferLength);

			return line;

		} catch (LineUnavailableException cause) {

			throw new ConfigurationException(cause);
		}
	}
}

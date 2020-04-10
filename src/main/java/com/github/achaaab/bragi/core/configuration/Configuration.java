package com.github.achaaab.bragi.core.configuration;

import com.github.achaaab.bragi.gui.configuration.ConfigurationView;
import org.slf4j.Logger;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.Mixer;
import java.nio.ByteOrder;
import java.util.List;

import static javax.swing.SwingUtilities.invokeLater;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Jonathan Guéhenneux
 * @since 0.1.8
 */
public class Configuration {

	private static final Logger LOGGER = getLogger(Configuration.class);

	private final LineConfiguration inputConfiguration;
	private final LineConfiguration outputConfiguration;

	private ConfigurationView view;

	/**
	 * Creates a new synthesizer.
	 */
	public Configuration() {

		inputConfiguration = new LineConfiguration(Mixer::getTargetLineInfo);
		outputConfiguration = new LineConfiguration(Mixer::getSourceLineInfo);

		invokeLater(() -> view = new ConfigurationView(this));
	}

	/**
	 * @return configuration of the input line
	 */
	public LineConfiguration inputConfiguration() {
		return inputConfiguration;
	}

	/**
	 * @return configuration of the output line
	 */
	public LineConfiguration outputConfiguration() {
		return outputConfiguration;
	}
}
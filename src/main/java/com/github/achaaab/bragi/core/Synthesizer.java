package com.github.achaaab.bragi.core;

import com.github.achaaab.bragi.core.configuration.Configuration;
import com.github.achaaab.bragi.gui.SynthesizerView;

import static javax.swing.SwingUtilities.invokeLater;

/**
 * @author Jonathan Guéhenneux
 * @since 0.1.8
 */
public class Synthesizer {

	private final Configuration configuration;

	/**
	 * Creates a synthesizer.
	 */
	public Synthesizer() {

		configuration = new Configuration();

		invokeLater(() -> new SynthesizerView(this));
	}

	/**
	 * @return configuration of this synthesizer
	 */
	public Configuration configuration() {
		return configuration;
	}
}
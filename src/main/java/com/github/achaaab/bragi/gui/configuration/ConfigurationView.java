package com.github.achaaab.bragi.gui.configuration;

import com.github.achaaab.bragi.core.configuration.Configuration;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.GridLayout;

import static javax.swing.BorderFactory.createTitledBorder;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

/**
 * synthesizer configuration view
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.8
 */
public class ConfigurationView extends JPanel {

	/**
	 * @param model configuration model
	 */
	public ConfigurationView(Configuration model) {

		var inputView = model.inputConfiguration().view();
		var outputView = model.outputConfiguration().view();

		inputView.setBorder(createTitledBorder("Input"));
		outputView.setBorder(createTitledBorder("Output"));

		setLayout(new GridLayout(2, 1));
		add(inputView);
		add(outputView);

		var frame = new JFrame("Synthesizer");
		frame.setSize(600, 240);
		frame.setContentPane(this);
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
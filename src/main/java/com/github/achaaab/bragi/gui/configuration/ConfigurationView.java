package com.github.achaaab.bragi.gui.configuration;

import com.github.achaaab.bragi.core.configuration.Configuration;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.GridLayout;

import static com.github.achaaab.bragi.gui.common.ViewScale.scale;
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

		var layout = new GridLayout(2, 1);
		layout.setVgap(scale(15));
		setLayout(layout);

		add(inputView);
		add(outputView);

		scale(this);

		var frame = new JFrame("Synthesizer");
		frame.setContentPane(this);
		frame.pack();
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
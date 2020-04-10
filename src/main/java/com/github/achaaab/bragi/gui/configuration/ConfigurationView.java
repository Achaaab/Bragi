package com.github.achaaab.bragi.gui.configuration;

import com.github.achaaab.bragi.core.configuration.Configuration;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.GridLayout;

import static com.github.achaaab.bragi.gui.common.ViewScale.scale;
import static java.awt.Color.BLACK;
import static java.awt.Color.DARK_GRAY;
import static java.awt.Color.RED;
import static javax.swing.BorderFactory.createLineBorder;
import static javax.swing.BorderFactory.createTitledBorder;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;
import static javax.swing.border.TitledBorder.LEFT;
import static javax.swing.border.TitledBorder.TOP;

/**
 * synthesizer configuration view
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.8
 */
public class ConfigurationView extends JPanel {

	private static final Color TITLE_COLOR = new Color(48, 96, 255);

	/**
	 * @param model configuration model
	 */
	public ConfigurationView(Configuration model) {

		var inputView = model.inputConfiguration().view();
		var outputView = model.outputConfiguration().view();

		var lineBorder = createLineBorder(DARK_GRAY, scale(2), true);

		var inputBorder = createTitledBorder(lineBorder, "Input", LEFT, TOP);
		var outputBorder = createTitledBorder(lineBorder, "Output", LEFT, TOP);

		var titleFont = inputBorder.getTitleFont();
		var scaledTitleFont = scale(titleFont, 1.2f);

		inputBorder.setTitleFont(scaledTitleFont);
		outputBorder.setTitleFont(scaledTitleFont);

		inputBorder.setTitleColor(TITLE_COLOR);
		outputBorder.setTitleColor(TITLE_COLOR);

		inputView.setBorder(inputBorder);
		outputView.setBorder(outputBorder);

		var layout = new GridLayout(2, 1);
		layout.setVgap(scale(15));
		setLayout(layout);

		add(inputView);
		add(outputView);

		scale(this);

		var frame = new JFrame("Configuration");
		frame.setContentPane(this);
		frame.pack();
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
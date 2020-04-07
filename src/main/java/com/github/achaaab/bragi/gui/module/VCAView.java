package com.github.achaaab.bragi.gui.module;

import com.github.achaaab.bragi.module.transformer.VCA;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;

import java.awt.BorderLayout;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

/**
 * VCA Swing view
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.4
 */
public class VCAView extends JPanel {

	/**
	 * @param model VCA model
	 */
	public VCAView(VCA model) {

		var initialGainSlider = new JSlider(-60, 20);
		initialGainSlider.setMajorTickSpacing(10);
		initialGainSlider.setPaintTicks(true);
		initialGainSlider.setPaintLabels(true);
		initialGainSlider.setBorder(new TitledBorder("Initial gain (dB)"));

		initialGainSlider.setValue(model.getInitialGain());
		initialGainSlider.addChangeListener(event -> model.setInitialGain(initialGainSlider.getValue()));

		setLayout(new BorderLayout());
		add(initialGainSlider);

		var frame = new JFrame(model.name());
		frame.setContentPane(this);
		frame.setSize(400, 200);
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
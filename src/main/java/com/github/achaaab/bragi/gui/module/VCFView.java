package com.github.achaaab.bragi.gui.module;

import com.github.achaaab.bragi.core.module.transformer.VCF;
import com.github.achaaab.bragi.gui.component.FrequencySlider;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import java.awt.GridLayout;

import static java.lang.Math.round;
import static javax.swing.SwingConstants.HORIZONTAL;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

/**
 * VCF Swing view
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.4
 */
public class VCFView extends JPanel {

	/**
	 * @param model VCF model
	 */
	public VCFView(VCF model) {

		var cutOffFrequencySlider = new FrequencySlider(10, 11);
		cutOffFrequencySlider.setDecimalValue(model.getCutoffFrequency());

		var emphasisSlider = new JSlider(HORIZONTAL, 0, 100, round(100 * model.getEmphasis()));
		emphasisSlider.setMajorTickSpacing(25);
		emphasisSlider.setMinorTickSpacing(5);
		emphasisSlider.setPaintTicks(true);
		emphasisSlider.setPaintLabels(true);
		emphasisSlider.setBorder(new TitledBorder("Emphasis (%)"));

		setLayout(new GridLayout(2, 1));
		add(cutOffFrequencySlider);
		add(emphasisSlider);

		cutOffFrequencySlider.addChangeListener(event ->
				model.setCutoffFrequency((float) cutOffFrequencySlider.getDecimalValue()));

		emphasisSlider.addChangeListener(event ->
				model.setEmphasis((float) emphasisSlider.getValue() / 100));

		var frame = new JFrame(model.name());
		frame.setSize(700, 250);
		frame.setContentPane(this);
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
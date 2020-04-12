package com.github.achaaab.bragi.gui.module;

import com.github.achaaab.bragi.core.module.transformer.Mixer;

import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import java.awt.Dimension;
import java.awt.GridLayout;

import static com.github.achaaab.bragi.gui.common.ViewScale.scale;

/**
 * mixer Swing view
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.8
 */
public class MixerView extends JPanel {

	private static final Dimension SLIDERS_SIZE = scale(new Dimension(350, 60));

	/**
	 * @param model mixer model
	 */
	public MixerView(Mixer model) {

		var gain0Slider = new JSlider(-60, 20);
		gain0Slider.setMajorTickSpacing(10);
		gain0Slider.setPaintTicks(true);
		gain0Slider.setPaintLabels(true);
		gain0Slider.setBorder(new TitledBorder("Input 0 gain (dB)"));

		var gain1Slider = new JSlider(-60, 20);
		gain1Slider.setMajorTickSpacing(10);
		gain1Slider.setPaintTicks(true);
		gain1Slider.setPaintLabels(true);
		gain1Slider.setBorder(new TitledBorder("Input 1 gain (dB)"));

		gain0Slider.setPreferredSize(SLIDERS_SIZE);
		gain1Slider.setPreferredSize(SLIDERS_SIZE);

		gain0Slider.setValue(model.getGain0());
		gain1Slider.setValue(model.getGain1());

		gain0Slider.addChangeListener(event -> model.setGain0(gain0Slider.getValue()));
		gain1Slider.addChangeListener(event -> model.setGain1(gain1Slider.getValue()));

		var layout = new GridLayout(2, 1);
		setLayout(layout);

		add(gain0Slider);
		add(gain1Slider);
	}
}
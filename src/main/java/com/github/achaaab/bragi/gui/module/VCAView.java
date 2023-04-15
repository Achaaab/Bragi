package com.github.achaaab.bragi.gui.module;

import com.github.achaaab.bragi.core.module.transformer.VCA;

import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import java.awt.Dimension;

import static com.github.achaaab.bragi.gui.common.ViewScale.scale;

/**
 * VCA Swing view
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.4
 */
public class VCAView extends JPanel {

	private static final Dimension SLIDERS_SIZE = scale(new Dimension(350, 60));

	/**
	 * @param model VCA model
	 * @since 0.2.0
	 */
	public VCAView(VCA model) {

		var initialGainSlider = new JSlider(-60, 20);
		initialGainSlider.setMajorTickSpacing(10);
		initialGainSlider.setPaintTicks(true);
		initialGainSlider.setPaintLabels(true);
		initialGainSlider.setBorder(new TitledBorder("Initial gain (dB)"));

		initialGainSlider.setPreferredSize(SLIDERS_SIZE);

		initialGainSlider.setValue(model.getInitialGain());
		initialGainSlider.addChangeListener(event -> model.setInitialGain(initialGainSlider.getValue()));

		setLayout(new BorderLayout());
		add(initialGainSlider);
	}
}

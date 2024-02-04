package com.github.achaaab.bragi.gui.module;

import com.github.achaaab.bragi.core.module.producer.Vco;
import com.github.achaaab.bragi.gui.component.WaveformComboBox;

import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import java.awt.Dimension;

import static com.github.achaaab.bragi.gui.common.ViewScale.scale;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;

/**
 * VCO Swing view
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.4
 */
public class VcoView extends JPanel {

	private static final Dimension SLIDERS_SIZE = scale(new Dimension(350, 60));

	/**
	 * @param model VCO model
	 * @since 0.2.0
	 */
	public VcoView(Vco model) {

		var waveform = model.getWaveform();
		var octave = model.getOctave();

		var waveformComboBox = new WaveformComboBox();
		waveformComboBox.setSelectedItem(waveform);

		var octaveSlider = new JSlider(-4, 4, octave);
		octaveSlider.setBorder(new TitledBorder("Octave"));
		octaveSlider.setMajorTickSpacing(1);
		octaveSlider.setPaintTicks(true);
		octaveSlider.setPaintLabels(true);

		octaveSlider.setPreferredSize(SLIDERS_SIZE);

		waveformComboBox.addActionListener(actionEvent -> model.setWaveform(waveformComboBox.getSelectedWaveform()));
		octaveSlider.addChangeListener(changeEvent -> model.setOctave(octaveSlider.getValue()));

		setLayout(new BorderLayout());
		add(waveformComboBox, NORTH);
		add(octaveSlider, CENTER);
	}
}

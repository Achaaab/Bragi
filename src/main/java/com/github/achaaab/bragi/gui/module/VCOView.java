package com.github.achaaab.bragi.gui.module;

import com.github.achaaab.bragi.gui.component.WaveformComboBox;
import com.github.achaaab.bragi.module.VCO;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

/**
 * VCO view
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.4
 */
public class VCOView extends JPanel {

	/**
	 * @param model VCO model
	 */
	public VCOView(VCO model) {

		var waveform = model.getWaveform();
		var octave = model.getOctave();

		var waveformComboBox = new WaveformComboBox();
		waveformComboBox.setSelectedItem(waveform);

		var octaveSlider = new JSlider(-4, 4, octave);
		octaveSlider.setBorder(new TitledBorder("Octave"));
		octaveSlider.setMajorTickSpacing(1);
		octaveSlider.setPaintTicks(true);
		octaveSlider.setPaintLabels(true);

		waveformComboBox.addActionListener(actionEvent -> model.setWaveform(waveformComboBox.getSelectedWaveform()));
		octaveSlider.addChangeListener(changeEvent -> model.setOctave(octaveSlider.getValue()));

		setLayout(new BorderLayout());
		add(waveformComboBox, NORTH);
		add(octaveSlider, CENTER);

		var frame = new JFrame(model.getName());
		frame.setContentPane(this);
		frame.setSize(400, 300);
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
package com.github.achaaab.bragi.gui.module;

import com.github.achaaab.bragi.gui.component.FrequencySlider;
import com.github.achaaab.bragi.gui.component.LinearRangeSlider;
import com.github.achaaab.bragi.gui.component.WaveformComboBox;
import com.github.achaaab.bragi.module.producer.LFO;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.GridLayout;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

/**
 * LFO Swing view
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.0
 */
public class LFOView extends JPanel {

	/**
	 * @param model LFO model
	 */
	public LFOView(LFO model) {

		var waveformComboBox = new WaveformComboBox();

		// from A-9 to A3
		var frequencySlider = new FrequencySlider(220.0 / (1 << 12), 12);

		var amplitudeSlider = new LinearRangeSlider(-5.0, 5.0, 100);
		amplitudeSlider.setMajorTickSpacing(10);
		amplitudeSlider.setPaintTicks(true);
		amplitudeSlider.setPaintLabels(true);
		amplitudeSlider.setBorder(BorderFactory.createTitledBorder("Amplitude in volts"));

		waveformComboBox.setSelectedItem(model.getWaveform());
		frequencySlider.setDecimalValue(model.getFrequency());
		amplitudeSlider.setDecimalLowerValue(model.getLowerPeak());
		amplitudeSlider.setDecimalUpperValue(model.getUpperPeak());

		waveformComboBox.addActionListener(event -> model.setWaveform(waveformComboBox.getSelectedWaveform()));
		frequencySlider.addChangeListener(changeEvent -> model.setFrequency(frequencySlider.getDecimalValue()));

		amplitudeSlider.addChangeListener(event -> {
			model.setLowerPeak((float) amplitudeSlider.getDecimalLowerValue());
			model.setUpperPeak((float) amplitudeSlider.getDecimalUpperValue());
		});

		setLayout(new GridLayout(3, 1));
		add(waveformComboBox);
		add(frequencySlider);
		add(amplitudeSlider);

		var frame = new JFrame(model.name());
		frame.setSize(600, 300);
		frame.setContentPane(this);
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
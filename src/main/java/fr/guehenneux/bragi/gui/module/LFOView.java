package fr.guehenneux.bragi.gui.module;

import fr.guehenneux.bragi.gui.component.FrequencySlider;
import fr.guehenneux.bragi.gui.component.WaveformComboBox;
import fr.guehenneux.bragi.module.LFO;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

/**
 * @author Jonathan Guéhenneux
 */
public class LFOView extends JPanel {

	/**
	 * @param model
	 */
	public LFOView(LFO model) {

		var waveformComboBox = new WaveformComboBox();
		waveformComboBox.setSelectedItem(model.getWaveform());
		waveformComboBox.addActionListener(event -> model.setWaveform(waveformComboBox.getSelectedWaveform()));

		// from 0.05Hz to 204.8Hz
		var frequencySlider = new FrequencySlider(0.05, 12);
		frequencySlider.setFrequency(model.getFrequency());
		frequencySlider.addChangeListener(changeEvent -> model.setFrequency(frequencySlider.getFrequency()));

		setLayout(new BorderLayout());
		add(waveformComboBox, NORTH);
		add(frequencySlider, CENTER);

		var frame = new JFrame(model.getName());
		frame.setSize(400, 300);
		frame.setContentPane(this);
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
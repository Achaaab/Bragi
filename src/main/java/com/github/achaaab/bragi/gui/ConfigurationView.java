package com.github.achaaab.bragi.gui;

import com.github.achaaab.bragi.core.Configuration;
import com.github.achaaab.bragi.gui.common.LabelCellRenderer;

import javax.sound.sampled.Mixer;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;

import static com.github.achaaab.bragi.core.Configuration.INPUT_MIXERS;
import static com.github.achaaab.bragi.core.Configuration.OUTPUT_MIXERS;
import static com.github.achaaab.bragi.core.Configuration.SAMPLE_RATES;
import static javax.swing.BorderFactory.createTitledBorder;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

/**
 * synthesizer configuration view
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.8
 */
public class ConfigurationView extends JPanel {

	private final Configuration model;

	private final JComboBox<Mixer> inputMixerComboBox;
	private final JComboBox<Mixer> outputMixerComboBox;

	/**
	 * @param model configuration model
	 */
	public ConfigurationView(Configuration model) {

		this.model = model;

		var mixerCellRenderer = new LabelCellRenderer<Mixer>(mixer -> mixer.getMixerInfo().getDescription());

		var inputPanel = new JPanel();
		var outputPanel = new JPanel();

		inputPanel.setBorder(createTitledBorder("Input"));
		outputPanel.setBorder(createTitledBorder("Output"));

		inputMixerComboBox = new JComboBox<>(INPUT_MIXERS);
		outputMixerComboBox = new JComboBox<>(OUTPUT_MIXERS);

		inputMixerComboBox.setRenderer(mixerCellRenderer);
		outputMixerComboBox.setRenderer(mixerCellRenderer);

		inputMixerComboBox.addItemListener(this::inputMixerSelected);
		outputMixerComboBox.addItemListener(this::outputMixerSelected);

		var inputSampleRateComboBox = new JComboBox<>(SAMPLE_RATES);
		var outputSampleRateComboBox = new JComboBox<>(SAMPLE_RATES);

		inputPanel.add(inputMixerComboBox);
		outputPanel.add(outputMixerComboBox);

		inputPanel.add(inputSampleRateComboBox);
		outputPanel.add(outputSampleRateComboBox);

		setLayout(new GridLayout(2, 1));
		add(inputPanel);
		add(outputPanel);

		var frame = new JFrame("Synthesizer");
		frame.setSize(600, 240);
		frame.setContentPane(this);
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	/**
	 * @param event input mixer selection event
	 */
	private void inputMixerSelected(ItemEvent event) {

		model.setInputMixer(inputMixerComboBox.getItemAt(inputMixerComboBox.getSelectedIndex()));

		// TODO reload parameters
	}

	/**
	 * @param event output mixer selection event
	 */
	private void outputMixerSelected(ItemEvent event) {

		model.setOutputMixer(outputMixerComboBox.getItemAt(outputMixerComboBox.getSelectedIndex()));

		// TODO reload parameters
	}
}
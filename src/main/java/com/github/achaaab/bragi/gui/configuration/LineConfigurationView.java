package com.github.achaaab.bragi.gui.configuration;

import com.github.achaaab.bragi.core.configuration.LineConfiguration;
import com.github.achaaab.bragi.gui.common.LabelCellRenderer;

import javax.sound.sampled.Mixer;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import java.awt.event.ItemEvent;

/**
 * line configuration view
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.8
 */
public class LineConfigurationView extends JPanel {

	private final LineConfiguration model;
	private final JComboBox<Mixer> mixerComboBox;

	/**
	 * @param model configuration model
	 */
	public LineConfigurationView(LineConfiguration model) {

		this.model = model;

		var mixerCellRenderer = new LabelCellRenderer<Mixer>(mixer -> mixer.getMixerInfo().getDescription());

		mixerComboBox = new JComboBox<>(model.getMixers());
		mixerComboBox.setRenderer(mixerCellRenderer);
		mixerComboBox.addItemListener(this::mixerSelected);

		var inputSampleRateComboBox = new JComboBox<>(model.getSampleRates());

		add(mixerComboBox);
		add(inputSampleRateComboBox);
	}

	/**
	 * @param event mixer selection event
	 */
	private void mixerSelected(ItemEvent event) {

		model.setMixer(mixerComboBox.getItemAt(mixerComboBox.getSelectedIndex()));

		// TODO reload parameters
	}
}
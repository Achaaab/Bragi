package com.github.achaaab.bragi.gui.configuration;

import com.github.achaaab.bragi.core.configuration.LineConfiguration;
import com.github.achaaab.bragi.gui.common.LabelCellRenderer;
import com.github.achaaab.bragi.gui.common.ViewScale;

import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.Mixer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.nio.ByteOrder;

import static com.github.achaaab.bragi.gui.common.ViewScale.scale;
import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.WEST;

/**
 * line configuration view
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.8
 */
public class LineConfigurationView extends JPanel {

	private final LineConfiguration model;

	private final JComboBox<Mixer> mixerComboBox;

	private final JComboBox<Integer> sampleRateComboBox;
	private final JComboBox<Integer> channelCountComboBox;
	private final JComboBox<Integer> sampleSizeComboBox;
	private final JComboBox<Encoding> encodingComboBox;
	private final JComboBox<ByteOrder> byteOrderComboBox;

	/**
	 * @param model configuration model
	 */
	public LineConfigurationView(LineConfiguration model) {

		this.model = model;

		var mixerCellRenderer = new LabelCellRenderer<Mixer>(mixer -> mixer.getMixerInfo().getDescription());

		mixerComboBox = new JComboBox<>(model.getMixers());
		mixerComboBox.setRenderer(mixerCellRenderer);
		mixerComboBox.setSelectedItem(model.getMixer());
		mixerComboBox.addItemListener(this::mixerSelected);

		sampleRateComboBox = new JComboBox<>(model.getSampleRates());
		channelCountComboBox = new JComboBox<>(model.getChannelCounts());
		sampleSizeComboBox = new JComboBox<>(model.getSampleSizes());
		encodingComboBox = new JComboBox<>(model.getEncodings());
		byteOrderComboBox = new JComboBox<>(model.getByteOrders());

		sampleRateComboBox.setSelectedItem(model.getSampleRate());
		channelCountComboBox.setSelectedItem(model.getChannelCount());
		sampleSizeComboBox.setSelectedItem(model.getSampleSize());
		encodingComboBox.setSelectedItem(model.getEncoding());
		byteOrderComboBox.setSelectedItem(model.getByteOrder());

		var layout = new GridBagLayout();
		setLayout(layout);

		var constraints = new GridBagConstraints();
		constraints.fill = HORIZONTAL;
		constraints.insets = new Insets(scale(3), scale(3), scale(3), scale(3));
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 1;

		constraints.anchor = WEST;
		constraints.gridx = 0;

		constraints.gridy = 0;
		add(new JLabel("Mixer"), constraints);

		constraints.gridy++;
		add(new JLabel("Sample rate in hertz (Hz)"), constraints);

		constraints.gridy++;
		add(new JLabel("Channel count"), constraints);

		constraints.gridy++;
		add(new JLabel("Sample size in bits (b)"), constraints);

		constraints.gridy++;
		add(new JLabel("Encoding"), constraints);

		constraints.gridy++;
		add(new JLabel("Byte order"), constraints);

		constraints.gridx++;
		add(byteOrderComboBox, constraints);

		constraints.gridy--;
		add(encodingComboBox, constraints);

		constraints.gridy--;
		add(sampleSizeComboBox, constraints);

		constraints.gridy--;
		add(channelCountComboBox, constraints);

		constraints.gridy--;
		add(sampleRateComboBox, constraints);

		constraints.gridy--;
		add(mixerComboBox, constraints);
	}

	/**
	 * @param event mixer selection event
	 */
	private void mixerSelected(ItemEvent event) {

		model.setMixer(mixerComboBox.getItemAt(mixerComboBox.getSelectedIndex()));

		// TODO reload parameters
	}
}
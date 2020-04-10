package com.github.achaaab.bragi.gui.configuration;

import com.github.achaaab.bragi.core.configuration.LineConfiguration;
import com.github.achaaab.bragi.gui.common.LabelCellRenderer;

import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.Mixer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
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

	private static final Color PROPERTY_COLOR = new Color(0, 0, 96);

	/**
	 * @param comboBox non-editable combo box
	 * @param <E>      combo box item type
	 * @return selected item
	 */
	private static <E> E getSelectedItem(JComboBox<E> comboBox) {
		return comboBox.getItemAt(comboBox.getSelectedIndex());
	}

	private final LineConfiguration model;

	private final JComboBox<Mixer> mixerComboBox;

	private final JComboBox<Integer> sampleRateComboBox;
	private final JComboBox<Integer> channelCountComboBox;
	private final JComboBox<Integer> sampleSizeComboBox;
	private final JComboBox<Encoding> encodingComboBox;
	private final JComboBox<ByteOrder> byteOrderComboBox;

	private boolean updateFlag;

	/**
	 * @param model configuration model
	 */
	public LineConfigurationView(LineConfiguration model) {

		this.model = model;

		var mixerCellRenderer = new LabelCellRenderer<Mixer>(mixer -> mixer.getMixerInfo().getDescription());

		mixerComboBox = new JComboBox<>(model.getMixers());

		sampleRateComboBox = new JComboBox<>();
		channelCountComboBox = new JComboBox<>();
		sampleSizeComboBox = new JComboBox<>();
		encodingComboBox = new JComboBox<>();
		byteOrderComboBox = new JComboBox<>();

		mixerComboBox.setForeground(PROPERTY_COLOR);
		sampleRateComboBox.setForeground(PROPERTY_COLOR);
		channelCountComboBox.setForeground(PROPERTY_COLOR);
		sampleSizeComboBox.setForeground(PROPERTY_COLOR);
		encodingComboBox.setForeground(PROPERTY_COLOR);
		byteOrderComboBox.setForeground(PROPERTY_COLOR);

		mixerComboBox.setRenderer(mixerCellRenderer);
		mixerComboBox.setSelectedItem(model.getMixer());
		updateProperties();

		mixerComboBox.addItemListener(this::mixerSelected);

		sampleRateComboBox.addItemListener(this::sampleRateSelected);
		channelCountComboBox.addItemListener(this::channelCountSelected);
		sampleSizeComboBox.addItemListener(this::sampleSizeSelected);
		encodingComboBox.addItemListener(this::encodingSelected);
		byteOrderComboBox.addItemListener(this::byteOrderSelected);

		var layout = new GridBagLayout();
		setLayout(layout);

		var constraints = new GridBagConstraints();
		constraints.fill = HORIZONTAL;
		constraints.insets = new Insets(scale(3), scale(3), scale(3), scale(3));

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

		constraints.weightx = 1;

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

		model.setMixer(getSelectedItem(mixerComboBox));
		updateProperties();
	}

	/**
	 * @param event sample rate selection event
	 */
	private void sampleRateSelected(ItemEvent event) {

		if (!updateFlag) {
			model.setSampleRate(getSelectedItem(sampleRateComboBox));
		}
	}

	/**
	 * @param event channel count selection event
	 */
	private void channelCountSelected(ItemEvent event) {

		if (!updateFlag) {
			model.setChannelCount(getSelectedItem(channelCountComboBox));
		}
	}

	/**
	 * @param event sample size selection event
	 */
	private void sampleSizeSelected(ItemEvent event) {

		if (!updateFlag) {
			model.setSampleSize(getSelectedItem(sampleSizeComboBox));
		}
	}

	/**
	 * @param event encoding selection event
	 */
	private void encodingSelected(ItemEvent event) {

		if (!updateFlag) {
			model.setEncoding(getSelectedItem(encodingComboBox));
		}
	}

	/**
	 * @param event byte order selection event
	 */
	private void byteOrderSelected(ItemEvent event) {

		if (!updateFlag) {
			model.setByteOrder(getSelectedItem(byteOrderComboBox));
		}
	}

	/**
	 * Reloads available and selected properties, after a mixer selection.
	 */
	private void updateProperties() {

		updateFlag = true;

		sampleRateComboBox.removeAllItems();
		channelCountComboBox.removeAllItems();
		sampleSizeComboBox.removeAllItems();
		encodingComboBox.removeAllItems();
		byteOrderComboBox.removeAllItems();

		for (var sampleRate : model.getSampleRates()) {
			sampleRateComboBox.addItem(sampleRate);
		}

		for (var channelCount : model.getChannelCounts()) {
			channelCountComboBox.addItem(channelCount);
		}

		for (var sampleSize : model.getSampleSizes()) {
			sampleSizeComboBox.addItem(sampleSize);
		}

		for (var encoding : model.getEncodings()) {
			encodingComboBox.addItem(encoding);
		}

		for (var byteOrder : model.getByteOrders()) {
			byteOrderComboBox.addItem(byteOrder);
		}

		sampleRateComboBox.setSelectedItem(model.getSampleRate());
		channelCountComboBox.setSelectedItem(model.getChannelCount());
		sampleSizeComboBox.setSelectedItem(model.getSampleSize());
		encodingComboBox.setSelectedItem(model.getEncoding());
		byteOrderComboBox.setSelectedItem(model.getByteOrder());

		updateFlag = false;
	}
}
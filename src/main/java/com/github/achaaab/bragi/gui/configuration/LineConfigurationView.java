package com.github.achaaab.bragi.gui.configuration;

import com.github.achaaab.bragi.core.configuration.LineConfiguration;
import com.github.achaaab.bragi.gui.common.LabelCellRenderer;

import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.Mixer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.nio.ByteOrder;
import java.util.function.Consumer;

import static com.github.achaaab.bragi.gui.common.ViewScale.scale;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.SOUTH;
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
	 * @param <E> combo box item type
	 * @return selected item
	 * @since 0.2.0
	 */
	private static <E> E getSelectedItem(JComboBox<E> comboBox) {
		return comboBox.getItemAt(comboBox.getSelectedIndex());
	}

	private LineConfiguration model;

	private final JComboBox<Mixer> mixerComboBox;
	private final JComboBox<Integer> sampleRateComboBox;
	private final JComboBox<Integer> channelCountComboBox;
	private final JComboBox<Integer> sampleSizeComboBox;
	private final JComboBox<Encoding> encodingComboBox;
	private final JComboBox<ByteOrder> byteOrderComboBox;

	private boolean updateFlag;
	private Consumer<LineConfiguration> onOk;
	private Runnable onCancel;

	/**
	 * Creates a new view for line configuration.
	 *
	 * @since 0.2.0
	 */
	public LineConfigurationView() {

		mixerComboBox = new JComboBox<>();
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

		var mixerCellRenderer = new LabelCellRenderer<Mixer>(mixer -> mixer.getMixerInfo().getDescription());
		mixerComboBox.setRenderer(mixerCellRenderer);

		mixerComboBox.addItemListener(this::mixerSelected);
		sampleRateComboBox.addItemListener(this::sampleRateSelected);
		channelCountComboBox.addItemListener(this::channelCountSelected);
		sampleSizeComboBox.addItemListener(this::sampleSizeSelected);
		encodingComboBox.addItemListener(this::encodingSelected);
		byteOrderComboBox.addItemListener(this::byteOrderSelected);

		var propertiesPanel = new JPanel();
		var propertiesLayout = new GridBagLayout();
		propertiesPanel.setLayout(propertiesLayout);

		var constraints = new GridBagConstraints();
		constraints.fill = HORIZONTAL;
		constraints.insets = new Insets(scale(3), scale(3), scale(3), scale(3));

		constraints.anchor = WEST;
		constraints.gridx = 0;

		constraints.gridy = 0;
		propertiesPanel.add(new JLabel("Mixer"), constraints);

		constraints.gridy++;
		propertiesPanel.add(new JLabel("Sample rate in hertz (Hz)"), constraints);

		constraints.gridy++;
		propertiesPanel.add(new JLabel("Channel count"), constraints);

		constraints.gridy++;
		propertiesPanel.add(new JLabel("Sample size in bits (b)"), constraints);

		constraints.gridy++;
		propertiesPanel.add(new JLabel("Encoding"), constraints);

		constraints.gridy++;
		propertiesPanel.add(new JLabel("Byte order"), constraints);

		constraints.weightx = 1;

		constraints.gridx++;
		propertiesPanel.add(byteOrderComboBox, constraints);

		constraints.gridy--;
		propertiesPanel.add(encodingComboBox, constraints);

		constraints.gridy--;
		propertiesPanel.add(sampleSizeComboBox, constraints);

		constraints.gridy--;
		propertiesPanel.add(channelCountComboBox, constraints);

		constraints.gridy--;
		propertiesPanel.add(sampleRateComboBox, constraints);

		constraints.gridy--;
		propertiesPanel.add(mixerComboBox, constraints);

		var okButton = new JButton("OK");
		var cancelButton = new JButton("Cancel");

		okButton.addActionListener(this::okPressed);
		cancelButton.addActionListener(this::cancelPressed);

		var buttonsPanel = new JPanel();
		buttonsPanel.add(okButton);
		buttonsPanel.add(cancelButton);

		var layout = new BorderLayout();
		setLayout(layout);
		add(propertiesPanel, CENTER);
		add(buttonsPanel, SOUTH);
	}

	/**
	 * @param model model to display
	 * @since 0.2.0
	 */
	public void display(LineConfiguration model) {

		this.model = model;

		updateFlag = true;
		mixerComboBox.removeAllItems();

		for (var mixer : model.mixers()) {
			mixerComboBox.addItem(mixer);
		}

		mixerComboBox.setSelectedItem(model.getMixer());
		updateProperties();
		updateFlag = false;
	}

	/**
	 * @param onOk consumer that consumes the line configuration when the OK button is pressed
	 * @since 0.2.0
	 */
	public void onOk(Consumer<LineConfiguration> onOk) {
		this.onOk = onOk;
	}

	/**
	 * @param onCancel task to run when the Cancel button is pressed
	 * @since 0.2.0
	 */
	public void onCancel(Runnable onCancel) {
		this.onCancel = onCancel;
	}

	/**
	 * @param event OK button pressed event
	 * @since 0.2.0
	 */
	private void okPressed(ActionEvent event) {

		if (onOk != null) {
			onOk.accept(model);
		}
	}

	/**
	 * @param event Cancel button pressed event
	 * @since 0.2.0
	 */
	private void cancelPressed(ActionEvent event) {

		if (onCancel != null) {
			onCancel.run();
		}
	}

	/**
	 * @param event mixer selection event
	 * @since 0.2.0
	 */
	private void mixerSelected(ItemEvent event) {

		if (!updateFlag) {

			model.setMixer(getSelectedItem(mixerComboBox));
			updateProperties();
		}
	}

	/**
	 * @param event sample rate selection event
	 * @since 0.2.0
	 */
	private void sampleRateSelected(ItemEvent event) {

		if (!updateFlag) {
			model.setSampleRate(getSelectedItem(sampleRateComboBox));
		}
	}

	/**
	 * @param event channel count selection event
	 * @since 0.2.0
	 */
	private void channelCountSelected(ItemEvent event) {

		if (!updateFlag) {
			model.setChannelCount(getSelectedItem(channelCountComboBox));
		}
	}

	/**
	 * @param event sample size selection event
	 * @since 0.2.0
	 */
	private void sampleSizeSelected(ItemEvent event) {

		if (!updateFlag) {
			model.setSampleSize(getSelectedItem(sampleSizeComboBox));
		}
	}

	/**
	 * @param event encoding selection event
	 * @since 0.2.0
	 */
	private void encodingSelected(ItemEvent event) {

		if (!updateFlag) {
			model.setEncoding(getSelectedItem(encodingComboBox));
		}
	}

	/**
	 * @param event byte order selection event
	 * @since 0.2.0
	 */
	private void byteOrderSelected(ItemEvent event) {

		if (!updateFlag) {
			model.setByteOrder(getSelectedItem(byteOrderComboBox));
		}
	}

	/**
	 * Reloads available and selected properties, after a mixer selection.
	 *
	 * @since 0.2.0
	 */
	private void updateProperties() {

		updateFlag = true;

		sampleRateComboBox.removeAllItems();
		channelCountComboBox.removeAllItems();
		sampleSizeComboBox.removeAllItems();
		encodingComboBox.removeAllItems();
		byteOrderComboBox.removeAllItems();

		for (var sampleRate : model.sampleRates()) {
			sampleRateComboBox.addItem(sampleRate);
		}

		for (var channelCount : model.channelCounts()) {
			channelCountComboBox.addItem(channelCount);
		}

		for (var sampleSize : model.sampleSizes()) {
			sampleSizeComboBox.addItem(sampleSize);
		}

		for (var encoding : model.encodings()) {
			encodingComboBox.addItem(encoding);
		}

		for (var byteOrder : model.byteOrders()) {
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

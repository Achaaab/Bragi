package com.github.achaaab.bragi.gui.component;

import com.github.achaaab.bragi.core.module.producer.wave.Waveform;

import javax.swing.JComboBox;
import javax.swing.border.TitledBorder;

/**
 * @author Jonathan Guéhenneux
 * @since 0.0.4
 */
public class WaveformComboBox extends JComboBox<Waveform> {

	/**
	 * Create a combo box for waveform selection.
	 *
	 * @since 0.2.0
	 */
	public WaveformComboBox() {

		super(Waveform.INSTANCES);

		setBorder(new TitledBorder("Waveform"));
	}

	/**
	 * @return selected waveform
	 * @since 0.2.0
	 */
	public Waveform getSelectedWaveform() {
		return getItemAt(getSelectedIndex());
	}
}

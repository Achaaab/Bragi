package com.github.achaaab.bragi.gui.component;

import com.github.achaaab.bragi.common.wave.Waveform;

import javax.swing.JComboBox;
import javax.swing.border.TitledBorder;

/**
 * @author Jonathan Guéhenneux
 * @since 0.0.4
 */
public class WaveformComboBox extends JComboBox<Waveform> {

	/**
	 * Create a combo box for waveform selection.
	 */
	public WaveformComboBox() {

		super(Waveform.INSTANCES);

		setBorder(new TitledBorder("Waveform"));
	}

	/**
	 * @return selected waveform
	 */
	public Waveform getSelectedWaveform() {
		return getItemAt(getSelectedIndex());
	}
}
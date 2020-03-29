package com.github.achaaab.bragi.gui.component;

import com.github.achaaab.bragi.common.wave.ReverseSawtooth;
import com.github.achaaab.bragi.common.wave.Pulse;
import com.github.achaaab.bragi.common.wave.Sawtooth;
import com.github.achaaab.bragi.common.wave.SawtoothTriangular;
import com.github.achaaab.bragi.common.wave.Sine;
import com.github.achaaab.bragi.common.wave.Triangle;
import com.github.achaaab.bragi.common.wave.Waveform;

import javax.swing.JComboBox;
import javax.swing.border.TitledBorder;

/**
 * @author Jonathan Guéhenneux
 * @since 0.0.4
 */
public class WaveformComboBox extends JComboBox<Waveform> {

	private static Waveform[] WAVEFORMS = {
			Sine.INSTANCE,
			Triangle.INSTANCE,
			Sawtooth.INSTANCE,
			ReverseSawtooth.INSTANCE,
			SawtoothTriangular.INSTANCE,
			Pulse.SQUARE,
			Pulse.PULSE_4,
			Pulse.PULSE_8
	};

	/**
	 * Create a combo box for waveform selection.
	 */
	public WaveformComboBox() {

		super(WAVEFORMS);

		setBorder(new TitledBorder("Waveform"));
	}

	/**
	 * @return selected waveform
	 */
	public Waveform getSelectedWaveform() {
		return getItemAt(getSelectedIndex());
	}
}

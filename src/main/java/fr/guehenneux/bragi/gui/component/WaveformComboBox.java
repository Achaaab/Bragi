package fr.guehenneux.bragi.gui.component;

import fr.guehenneux.bragi.common.wave.Pulse;
import fr.guehenneux.bragi.common.wave.ReverseSawtooth;
import fr.guehenneux.bragi.common.wave.Sawtooth;
import fr.guehenneux.bragi.common.wave.SawtoothTriangular;
import fr.guehenneux.bragi.common.wave.Sine;
import fr.guehenneux.bragi.common.wave.Triangle;
import fr.guehenneux.bragi.common.wave.Waveform;

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

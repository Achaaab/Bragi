package com.github.achaaab.bragi.common.wave;

/**
 * @author Jonathan Guéhenneux
 * @since 0.1.0
 */
public interface Waveform {

	Waveform[] INSTANCES = {
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
	 * @param periodFraction {@code periodFraction ∈ [0.0, 1.0[}
	 * @return sample at given fraction of waveform period
	 */
	float getSample(double periodFraction);
}
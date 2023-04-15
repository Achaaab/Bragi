package com.github.achaaab.bragi.core.module.producer.wave;

/**
 * The Reverse Sawtooth has a sound similar to the regular Sawtooth wave.
 * It can be used as a modulation source.
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.2
 */
public class ReverseSawtooth extends AbstractWaveform {

	/**
	 * @see #REVERSE_SAWTOOTH
	 * @since 0.2.0
	 */
	ReverseSawtooth() {
		super("Reverse Sawtooth");
	}

	@Override
	public float getSample(double periodFraction) {
		return (float) (UPPER_PEAK - AMPLITUDE * periodFraction);
	}
}

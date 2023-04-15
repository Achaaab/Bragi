package com.github.achaaab.bragi.core.module.producer.wave;

/**
 * @author Jonathan Guéhenneux
 * @since 0.1.0
 */
public class Sawtooth extends AbstractWaveform {

	/**
	 * @see #SAWTOOTH
	 * @since 0.2.0
	 */
	Sawtooth() {
		super("Sawtooth");
	}

	@Override
	public float getSample(double periodFraction) {
		return (float) (LOWER_PEAK + AMPLITUDE * periodFraction);
	}
}

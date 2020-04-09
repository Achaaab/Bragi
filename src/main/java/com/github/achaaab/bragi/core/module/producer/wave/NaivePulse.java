package com.github.achaaab.bragi.core.module.producer.wave;

/**
 * Naive pulse brutally switches between lower peak and upper peak.
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.2
 */
public class NaivePulse extends AbstractWaveform {

	private final double dutyCycle;

	/**
	 * @param name      naive pulse wave name
	 * @param dutyCycle fraction of one period where the signal is up, in range ]0.0, 1.0[
	 * @see #NAIVE_SQUARE
	 * @see #NAIVE_PULSE_4
	 * @see #NAIVE_PULSE_8
	 */
	public NaivePulse(String name, double dutyCycle) {

		super(name);

		this.dutyCycle = dutyCycle;
	}

	@Override
	public float getSample(double periodFraction) {
		return periodFraction < dutyCycle ? UPPER_PEAK : LOWER_PEAK;
	}
}
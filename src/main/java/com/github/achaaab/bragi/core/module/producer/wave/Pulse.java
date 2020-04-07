package com.github.achaaab.bragi.core.module.producer.wave;

/**
 * @author Jonathan Guéhenneux
 * @since 0.0.2
 */
public class Pulse extends NamedWaveform {

	private final double pulseFraction;

	/**
	 * @param name          pulse wave name
	 * @param pulseFraction pulse fraction in range ]0, 1[
	 * @see #SQUARE
	 * @see #PULSE_4
	 * @see #PULSE_8
	 */
	public Pulse(String name, double pulseFraction) {

		super(name);

		this.pulseFraction = pulseFraction;
	}

	@Override
	public float getSample(double periodFraction) {
		return periodFraction < pulseFraction ? UPPER_PEAK : LOWER_PEAK;
	}
}
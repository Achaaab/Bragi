package com.github.achaaab.bragi.common.wave;

/**
 * @author Jonathan Guéhenneux
 * @since 0.0.2
 */
public class Pulse extends BoundedWaveform {

	public static final Pulse SQUARE = new Pulse("Square", 0.5);
	public static final Pulse PULSE_4 = new Pulse("Rectangle 1/4", 0.25);
	public static final Pulse PULSE_8 = new Pulse("Rectangle 1/8", 0.125);

	private double pulseFraction;

	/**
	 * @param name pulse wave name
	 * @param pulseFraction pulse fraction in range ]0, 1[
	 */
	public Pulse(String name, double pulseFraction) {

		super(name);

		this.pulseFraction = pulseFraction;
	}

	@Override
	public float getSample(double periodFraction) {
		return periodFraction < pulseFraction ? maximum : minimum;
	}
}
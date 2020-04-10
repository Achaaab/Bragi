package com.github.achaaab.bragi.core.module.producer.wave;

import static java.lang.Math.PI;
import static java.lang.Math.cos;

/**
 * Way too hard to compute, especially at low frequencies.
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.7
 */
public class BandLimitedSquare extends AbstractWaveform {

	private static final double COSINE_PERIOD = 2 * PI;
	private static final int HARMONIC_COUNT = 50;

	/**
	 * @see #ANALOG_SQUARE
	 */
	BandLimitedSquare() {
		super("Analog Square");
	}

	@Override
	public float getSample(double periodFraction) {

		var a = -periodFraction * COSINE_PERIOD;
		var b = -PI / 2;
		var c = 2 * periodFraction * COSINE_PERIOD;

		var sample = 0.0;

		for (var harmonicIndex = 1; harmonicIndex <= HARMONIC_COUNT; harmonicIndex += 4) {
			sample += cos(a += c) / (b += PI) - cos(a += c) / (b += PI);
		}

		return (float) (0.5 + 0.8 * sample);
	}
}
package com.github.achaaab.bragi.core.module.producer.wave;

import com.github.achaaab.bragi.common.Normalizer;

import static java.lang.Math.PI;
import static java.lang.Math.sin;

/**
 * The Sine only has a fundamental frequency and no harmonics.
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.2
 */
public class Sine extends AbstractWaveform {

	private static final double SINE_PERIOD = 2 * PI;

	private final Normalizer normalizer;

	/**
	 * @see #SINE
	 */
	Sine() {

		super("Sine");

		normalizer = new Normalizer(
				(float) sin(-PI / 2), (float) sin(PI / 2),
				LOWER_PEAK, UPPER_PEAK
		);
	}

	@Override
	public float getSample(double periodFraction) {

		var t = periodFraction * SINE_PERIOD;
		return normalizer.normalize(sin(t));
	}
}
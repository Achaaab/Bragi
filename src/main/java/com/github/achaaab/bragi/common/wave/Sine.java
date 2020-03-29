package com.github.achaaab.bragi.common.wave;

import com.github.achaaab.bragi.common.Normalizer;

import static java.lang.Math.PI;
import static java.lang.Math.sin;

/**
 * The Sine only has a fundamental frequency and no harmonics.
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.2
 */
public class Sine extends BoundedWaveform {

	public static final Sine INSTANCE = new Sine();

	private static final double SINE_PERIOD = 2 * PI;

	private Normalizer normalizer;

	/**
	 * Use singleton.
	 */
	private Sine() {

		super("Sine");

		normalizer = new Normalizer(
				(float) sin(-PI / 2), (float) sin(PI / 2),
				minimum, maximum
		);
	}

	@Override
	public float getSample(double periodFraction) {

		var t = periodFraction * SINE_PERIOD;
		return normalizer.normalize(sin(t));
	}
}
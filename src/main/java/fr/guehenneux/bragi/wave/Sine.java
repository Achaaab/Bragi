package fr.guehenneux.bragi.wave;

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

	private static final double SINE_MINIMUM = sin(-PI / 2);
	private static final double SINE_MAXIMUM = sin(PI / 2);
	private static final double SINE_AMPLITUDE = SINE_MAXIMUM - SINE_MINIMUM;
	private static final double SINE_PERIOD = 2 * PI;

	/**
	 * Use singleton.
	 */
	private Sine() {
		super("Sine");
	}

	@Override
	public float getSample(double periodFraction) {

		// t ∈ [0.0, SINE_PERIOD[
		var t = periodFraction * SINE_PERIOD;

		// natural sine ∈ [SINE_MINIMUM, SINE_MAXIMUM[
		var naturalSine = sin(t);

		// normalized sine ∈ [0, 1.0[
		var normalizedSine = (naturalSine - SINE_MINIMUM) / SINE_AMPLITUDE;

		// bounded sine ∈ [minimum, maximum[
		var boundedSine = minimum + normalizedSine * amplitude;

		return (float) boundedSine;
	}
}
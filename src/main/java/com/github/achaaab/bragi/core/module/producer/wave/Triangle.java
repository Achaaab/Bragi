package com.github.achaaab.bragi.core.module.producer.wave;

/**
 * The Triangle wave has an extremely strong fundamental, yet contains only odd-numbered harmonics at very low levels.
 * This makes the Triangle wave an ideal choice for creating soft, flute-like sounds
 * that have a pure tone with little overtone activity.
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.2
 */
public class Triangle extends NamedWaveform {

	/**
	 * @see #TRIANGLE
	 */
	Triangle() {
		super("Triangle");
	}

	@Override
	public float getSample(double periodFraction) {

		return (float) (periodFraction < HALF_PERIOD ?
				UPPER_PEAK - 2 * AMPLITUDE * periodFraction :
				LOWER_PEAK + 2 * AMPLITUDE * (periodFraction - HALF_PERIOD));
	}
}
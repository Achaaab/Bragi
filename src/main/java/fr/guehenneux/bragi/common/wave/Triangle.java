package fr.guehenneux.bragi.common.wave;

/**
 * The Triangle wave has an extremely strong fundamental, yet contains only odd-numbered harmonics at very low levels.
 * This makes the Triangle wave an ideal choice for creating soft, flute-like sounds
 * that have a pure tone with little overtone activity.
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.2
 */
public class Triangle extends BoundedWaveform {

	public static final Triangle INSTANCE = new Triangle();

	private static final double HALF_PERIOD = 0.5;

	/**
	 * Use singleton.
	 */
	private Triangle() {
		super("Triangle");
	}

	@Override
	public float getSample(double periodFraction) {

		return (float) (periodFraction < HALF_PERIOD ?
				maximum - 2 * amplitude * periodFraction :
				minimum + 2 * amplitude * (periodFraction - HALF_PERIOD));
	}
}
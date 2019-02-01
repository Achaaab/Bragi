package fr.guehenneux.bragi.wave;

/**
 * @author Jonathan Guéhenneux
 */
public class Triangle implements Waveform {

	public static final Triangle INSTANCE = new Triangle();

	/**
	 * Use singleton.
	 */
	private Triangle() {

	}

	@Override
	public float getSample(double periodFraction) {
		return (float) (periodFraction < 0.5 ? 1 - 4 * periodFraction : 4 * periodFraction - 3);
	}
}
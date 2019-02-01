package fr.guehenneux.bragi.wave;

/**
 * @author Jonathan Guéhenneux
 */
public class Sine implements  Waveform {

	public static final Sine INSTANCE = new Sine();

	private static final double PERIOD = 2 * Math.PI;

	/**
	 * Use singleton.
	 */
	private Sine() {

	}

	@Override
	public float getSample(double periodFraction) {
		return (float) Math.sin(periodFraction * PERIOD);
	}
}
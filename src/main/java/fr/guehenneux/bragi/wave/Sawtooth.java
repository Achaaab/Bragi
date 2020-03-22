package fr.guehenneux.bragi.wave;

/**
 * @author Jonathan Guéhenneux
 */
public class Sawtooth implements Waveform {

	public static final Sawtooth INSTANCE = new Sawtooth();

	/**
	 * Use singleton.
	 */
	private Sawtooth() {

	}

	@Override
	public float getSample(double periodFraction) {
		return (float) (2 * periodFraction - 1);
	}

	@Override
	public String toString() {
		return "Sawtooth";
	}
}
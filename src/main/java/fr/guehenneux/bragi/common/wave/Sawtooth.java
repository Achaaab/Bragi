package fr.guehenneux.bragi.common.wave;

/**
 * @author Jonathan Guéhenneux
 */
public class Sawtooth extends BoundedWaveform {

	public static final Sawtooth INSTANCE = new Sawtooth();

	/**
	 * Use singleton.
	 */
	private Sawtooth() {
		super("Sawtooth");
	}

	@Override
	public float getSample(double periodFraction) {
		return (float) (minimum + amplitude * periodFraction);
	}
}
package fr.guehenneux.bragi.common.wave;

/**
 * The Reverse Sawtooth has a sound similar to the regular Sawtooth wave.
 * It can be use as a modulation source.
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.2
 */
public class ReverseSawtooth extends BoundedWaveform {

	public static final ReverseSawtooth INSTANCE = new ReverseSawtooth();

	/**
	 * Use singleton.
	 */
	private ReverseSawtooth() {
		super("Reverse Sawtooth");
	}

	@Override
	public float getSample(double periodFraction) {
		return (float) (maximum - amplitude * periodFraction);
	}
}
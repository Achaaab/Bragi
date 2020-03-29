package com.github.achaaab.bragi.common.wave;

/**
 * @author Jonathan Guéhenneux
 * @since 0.1.0
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
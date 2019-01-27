package fr.guehenneux.bragi.wave;

/**
 * @author Jonathan Guéhenneux
 */
public class SquareWave extends PulseWave {

	/**
	 * @param frequency
	 */
	public SquareWave(double frequency) {
		super(0.5f, frequency);
	}
}
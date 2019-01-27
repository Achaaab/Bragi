package fr.guehenneux.bragi.module;

/**
 * @author Jonathan Guéhenneux
 */
public class Key {

	private double frequency;

	/**
	 * @param frequency key frequency in hertz
	 */
	public Key(double frequency) {
		this.frequency = frequency;
	}

	/**
	 * @return key frequency in hertz
	 */
	public double getFrequency() {
		return frequency;
	}
}
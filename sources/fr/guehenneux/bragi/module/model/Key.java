package fr.guehenneux.bragi.module.model;

/**
 * @author Jonathan Guéhenneux
 */
public class Key {

	private String name;
	private double frequency;
	private int code;

	/**
	 * @param name key name
	 * @param frequency key frequency in hertz
	 * @param code key code
	 */
	public Key(String name, double frequency, int code) {

		this.name = name;
		this.frequency = frequency;
		this.code = code;
	}

	/**
	 * @return key frequency in hertz
	 */
	public double getFrequency() {
		return frequency;
	}

	/**
	 * @return key code
	 */
	public int getCode() {
		return code;
	}

	@Override
	public String toString() {
		return name;
	}
}
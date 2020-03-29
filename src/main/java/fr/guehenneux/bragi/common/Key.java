package fr.guehenneux.bragi.common;

/**
 * @author Jonathan Guéhenneux
 * @since 0.0.6
 */
public class Key {

	private String name;
	private float voltage;
	private int code;

	/**
	 * @param name key name
	 * @param voltage voltage output
	 * @param code key code
	 */
	public Key(String name, float voltage, int code) {

		this.name = name;
		this.voltage = voltage;
		this.code = code;
	}

	/**
	 * @return voltage output
	 */
	public float getVoltage() {
		return voltage;
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
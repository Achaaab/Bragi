package fr.guehenneux.bragi.wave;

/**
 * @author Jonathan Guéhenneux
 */
public class Pulse implements Waveform {

	public static final Pulse SQUARE = new Pulse("Square", 0.5);
	public static final Pulse PULSE_25 = new Pulse("Rectangle 1/4", 0.25);
	public static final Pulse PULSE_125 = new Pulse("Rectangle 1/8", 0.125);

	private String name;
	private double pulseFraction;

	/**
	 * @param name pulse wave name
	 * @param pulseFraction pulse fraction in range ]0, 1[
	 */
	public Pulse(String name, double pulseFraction) {

		this.name = name;
		this.pulseFraction = pulseFraction;
	}

	@Override
	public float getSample(double periodFraction) {
		return periodFraction < pulseFraction ? 1.0f : -1.0f;
	}

	@Override
	public String toString() {
		return name;
	}
}
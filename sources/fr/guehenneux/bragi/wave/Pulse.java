package fr.guehenneux.bragi.wave;

/**
 * @author Jonathan Guéhenneux
 */
public class Pulse implements Waveform {

	public static final Pulse SQUARE = new Pulse(0.5);
	public static final Pulse PULSE_25 = new Pulse(0.25);
	public static final Pulse PULSE_125 = new Pulse(0.125);

	private double pulseFraction;

	/**
	 * @param pulseFraction pulse fraction in range ]0, 1[
	 */
	public Pulse(double pulseFraction) {
		this.pulseFraction = pulseFraction;
	}

	@Override
	public float getSample(double periodFraction) {
		return periodFraction < pulseFraction ? 1.0f : -1.0f;
	}
}
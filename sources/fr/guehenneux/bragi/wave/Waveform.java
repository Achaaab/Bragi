package fr.guehenneux.bragi.wave;

/**
 * @author Jonathan Guéhenneux
 */
public interface Waveform {

	/**
	 * @param periodFraction period fraction in range [0, 1[
	 * @return sample at given fraction of waveform period
	 */
	float getSample(double periodFraction);
}
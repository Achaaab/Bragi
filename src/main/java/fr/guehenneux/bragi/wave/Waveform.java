package fr.guehenneux.bragi.wave;

/**
 * @author Jonathan Guéhenneux
 */
public interface Waveform {

	/**
	 * @param periodFraction {@code periodFraction ∈ [0.0, 1.0[}
	 * @return sample at given fraction of waveform period
	 */
	float getSample(double periodFraction);
}
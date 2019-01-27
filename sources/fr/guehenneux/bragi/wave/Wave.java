

package fr.guehenneux.bragi.wave;

/**
 * @author Jonathan Guéhenneux
 */
public interface Wave {

	/**
	 * @return wave frequency in hertz (number of oscillations per second)
	 */
	double getFrequency();

	/**
	 * @param frequency wave frequency in hertz (number of oscillations per second)
	 */
	void setFrequency(double frequency);

	/**
	 * @param sampleCount number of samples to generate
	 * @param sampleLength duration of a sample in seconds
	 * @return generated samples
	 */
	float[] getSamples(int sampleCount, double sampleLength);

	/**
	 * @param modulationSamples modulation samples
	 * @param sampleCount number of samples to generate
	 * @param sampleLength duration of a sample in seconds
	 * @return generated samples
	 */
	float[] getSamples(float[] modulationSamples, int sampleCount, double sampleLength);
}
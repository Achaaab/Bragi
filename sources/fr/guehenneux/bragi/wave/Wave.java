

package fr.guehenneux.bragi.wave;

/**
 * @author Jonathan Guéhenneux
 */
public class Wave {

	private Waveform waveform;
	protected double frequency;
	private double periodPercent;

	/**
	 * @param waveform waveform
	 * @param frequency frequency in hertz (number of oscillations per second)
	 */
	public Wave(Waveform waveform, double frequency) {

		this.waveform = waveform;
		this.frequency = frequency;

		periodPercent = 0;
	}

	/**
	 * @return wave frequency in hertz (number of oscillations per second)
	 */
	public double getFrequency() {
		return frequency;
	}

	/**
	 * @param frequency wave frequency in hertz (number of oscillations per second)
	 */
	public void setFrequency(double frequency) {
		this.frequency = frequency;
	}

	/**
	 * @param sampleCount number of samples to generate
	 * @param sampleLength duration of a sample in seconds
	 * @return generated samples
	 */
	public float[] getSamples(int sampleCount, double sampleLength) {

		float[] samples = new float[sampleCount];
		float sample;

		for (int sampleIndex = 0; sampleIndex < sampleCount; sampleIndex++) {

			sample = waveform.getSample(periodPercent);
			samples[sampleIndex] = sample;
			periodPercent = (periodPercent + sampleLength * frequency) % 1.0;
		}

		return samples;
	}

	/**
	 * @param modulationSamples modulation samples
	 * @param sampleCount number of samples to generate
	 * @param sampleLength duration of a sample in seconds
	 * @return generated samples
	 */
	public float[] getSamples(float[] modulationSamples, int sampleCount, double sampleLength) {

		float[] samples = new float[sampleCount];
		float sample;

		float modulationSample;
		double modulationFactor;

		for (int sampleIndex = 0; sampleIndex < sampleCount; sampleIndex++) {

			modulationSample = modulationSamples[sampleIndex];
			modulationFactor = Math.pow(2, modulationSample / 20);

			sample = waveform.getSample(periodPercent);
			samples[sampleIndex] = sample;
			periodPercent = (periodPercent + sampleLength * frequency * modulationFactor) % 1.0;
		}

		return samples;
	}
}
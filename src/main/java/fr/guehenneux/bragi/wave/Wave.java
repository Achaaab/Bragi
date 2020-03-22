package fr.guehenneux.bragi.wave;

import static java.lang.Math.pow;

/**
 * @author Jonathan Guéhenneux
 */
public class Wave {

	private Waveform waveform;
	protected double frequency;
	private double periodPercent;

	/**
	 * @param waveform  waveform
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
	 * @param waveform waveform to set
	 */
	public void setWaveform(Waveform waveform) {
		this.waveform = waveform;
	}

	/**
	 * @param modulationSamples modulation samples
	 * @param sampleCount       number of samples to generate
	 * @param sampleLength      duration of a sample in seconds
	 * @return generated samples
	 */
	public float[] getSamples(float[] modulationSamples, int sampleCount, double sampleLength) {

		var samples = new float[sampleCount];

		for (var sampleIndex = 0; sampleIndex < sampleCount; sampleIndex++) {

			samples[sampleIndex] = waveform.getSample(periodPercent);
			var modulationFactor = modulationSamples == null ? 1.0 : pow(2.0, modulationSamples[sampleIndex]);
			periodPercent = (periodPercent + sampleLength * frequency * modulationFactor) % 1.0;
		}

		return samples;
	}
}
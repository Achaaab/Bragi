package fr.guehenneux.bragi.wave;

/**
 * @author Jonathan Guéhenneux
 */
public class PulseWave implements Wave {

	private double frequency;
	private float pulsePercent;
	private double periodPercent;

	/**
	 * @param pulsePercent
	 * @param frequency
	 */
	public PulseWave(float pulsePercent, double frequency) {

		this.frequency = frequency;
		this.pulsePercent = pulsePercent;
		periodPercent = 0;
	}

	@Override
	public float[] getSamples(int sampleCount, double sampleLength) {

		float[] samples = new float[sampleCount];
		float sample;

		for (int sampleIndex = 0; sampleIndex < sampleCount; sampleIndex++) {

			periodPercent %= 1;

			sample = periodPercent < pulsePercent ? 1.0f : -1.0f;
			samples[sampleIndex] = sample;

			periodPercent += sampleLength * frequency;
		}

		return samples;
	}

	@Override
	public float[] getSamples(float[] modulationSamples, int sampleCount, double sampleLength) {

		float[] samples = new float[sampleCount];
		float sample;

		float modulationSample;
		double modulationFactor;

		for (int sampleIndex = 0; sampleIndex < sampleCount; sampleIndex++) {

			modulationSample = modulationSamples[sampleIndex];
			modulationFactor = Math.pow(2, modulationSample);

			periodPercent %= 1;

			sample = periodPercent < pulsePercent ? 1.0f : -1.0f;
			samples[sampleIndex] = sample;

			periodPercent += sampleLength * frequency * modulationFactor;
		}

		return samples;
	}

	@Override
	public void setFrequency(double frequency) {
		this.frequency = frequency;
	}

	@Override
	public double getFrequency() {
		return frequency;
	}
}
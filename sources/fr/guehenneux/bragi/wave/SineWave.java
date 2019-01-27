package fr.guehenneux.bragi.wave;

/**
 * @author Jonathan Guéhenneux
 */
public class SineWave implements Wave {

	public static final double SINE_PERIOD = 2 * Math.PI;

	private double frequency;
	private double periodPercent;

	/**
	 * @param frequency
	 */
	public SineWave(double frequency) {

		this.frequency = frequency;
		periodPercent = 0;
	}

	@Override
	public float[] getSamples(int sampleCount, double sampleLength) {

		float[] samples = new float[sampleCount];
		float sample;

		for (int sampleIndex = 0; sampleIndex < sampleCount; sampleIndex++) {

			periodPercent %= 1;

			sample = (float) Math.sin(periodPercent * SINE_PERIOD);
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

		double period;

		for (int sampleIndex = 0; sampleIndex < sampleCount; sampleIndex++) {

			modulationSample = modulationSamples[sampleIndex];
			modulationFactor = Math.pow(2, modulationSample);

			periodPercent %= 1;

			sample = (float) Math.sin(periodPercent * SINE_PERIOD);
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
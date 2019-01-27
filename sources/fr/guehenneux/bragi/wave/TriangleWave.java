

package fr.guehenneux.bragi.wave;

/**
 * @author Jonathan Guéhenneux
 */
public class TriangleWave implements Wave {

	private double frequency;
	private double periodPercent;

	/**
	 * @param frequency
	 */
	public TriangleWave(double frequency) {

		this.frequency = frequency;
		periodPercent = 0;
	}

	@Override
	public float[] getSamples(int sampleCount, double sampleLength) {

		float[] samples = new float[sampleCount];
		float sample;

		for (int sampleIndex = 0; sampleIndex < sampleCount; sampleIndex++) {

			periodPercent %= 1;

			sample = periodPercent < 0.5 ?
					(float) (1 - 4 * periodPercent) :
					(float) (4 * periodPercent - 3);

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

			sample = (float) (2 * periodPercent - 1);
			samples[sampleIndex] = sample;

			periodPercent += sampleLength * frequency * modulationFactor;
		}

		return samples;
	}

	public void setFrequency(double frequency) {
		this.frequency = frequency;
	}

	public double getFrequency() {
		return frequency;
	}
}
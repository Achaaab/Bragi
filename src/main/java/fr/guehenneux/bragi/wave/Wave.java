package fr.guehenneux.bragi.wave;

import fr.guehenneux.bragi.Settings;

import static java.lang.Math.fma;
import static java.lang.Math.pow;

/**
 * @author Jonathan Guéhenneux
 */
public class Wave {

	private Waveform waveform;
	private double frequency;

	private double sampleRate;
	private double periodPercent;

	/**
	 * @param waveform  waveform
	 * @param frequency frequency in hertz (number of oscillations per second)
	 */
	public Wave(Waveform waveform, double frequency) {

		this.waveform = waveform;
		this.frequency = frequency;

		sampleRate = Settings.INSTANCE.getFrameRate();
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
	 * @return current waveform
	 */
	public Waveform getWaveform() {
		return waveform;
	}

	/**
	 * @param waveform waveform to set
	 */
	public void setWaveform(Waveform waveform) {
		this.waveform = waveform;
	}

	/**
	 * @param octave            octave
	 * @param modulationSamples modulation samples
	 * @param sampleCount       number of samples to generate
	 * @return generated samples
	 */
	public float[] getSamples(int octave, float[] modulationSamples, int sampleCount) {

		var samples = new float[sampleCount];

		for (var sampleIndex = 0; sampleIndex < sampleCount; sampleIndex++) {

			samples[sampleIndex] = waveform.getSample(periodPercent);

			var modulationFactor = modulationSamples == null ? 1.0 :
					pow(2.0, octave + modulationSamples[sampleIndex]);

			periodPercent = fma(frequency, modulationFactor / sampleRate, periodPercent) % 1.0;
		}

		return samples;
	}
}
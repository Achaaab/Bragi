package com.github.achaaab.bragi.common.wave;

import com.github.achaaab.bragi.common.Settings;

import static com.github.achaaab.bragi.common.wave.Waveform.AMPLITUDE;
import static com.github.achaaab.bragi.common.wave.Waveform.LOWER_PEAK;
import static java.lang.Math.fma;
import static java.lang.Math.pow;

/**
 * @author Jonathan Guéhenneux
 * @since 0.1.0
 */
public class Wave {

	private final double sampleRate;

	private Waveform waveform;
	private double frequency;
	private double periodPercent;
	private float lowerPeak;
	private float upperPeak;
	private float amplitude;

	/**
	 * @param waveform  waveform
	 * @param frequency frequency in hertz (number of oscillations per second)
	 */
	public Wave(Waveform waveform, double frequency) {

		this.waveform = waveform;
		this.frequency = frequency;

		sampleRate = Settings.INSTANCE.frameRate();
		lowerPeak = Settings.INSTANCE.minimalVoltage();
		upperPeak = Settings.INSTANCE.maximalVoltage();

		amplitude = upperPeak - lowerPeak;
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
	 * @return lower peak in volts
	 * @since 0.1.3
	 */
	public float getLowerPeak() {
		return lowerPeak;
	}

	/**
	 * @param lowerPeak lower peak in volts
	 * @since 0.1.3
	 */
	public void setLowerPeak(float lowerPeak) {

		this.lowerPeak = lowerPeak;

		amplitude = upperPeak - lowerPeak;
	}

	/**
	 * @return upper peak in volts
	 * @since 0.1.3
	 */
	public float getUpperPeak() {
		return upperPeak;
	}

	/**
	 * @param upperPeak upper peak in volts
	 * @since 0.1.3
	 */
	public void setUpperPeak(float upperPeak) {

		this.upperPeak = upperPeak;

		amplitude = upperPeak - lowerPeak;
	}

	/**
	 * @param octave            octave adjustment
	 * @param modulationSamples modulation samples in volts
	 * @param sampleCount       number of samples to generate
	 * @return generated samples in volts
	 */
	public float[] getSamples(int octave, float[] modulationSamples, int sampleCount) {

		var samples = new float[sampleCount];

		for (var sampleIndex = 0; sampleIndex < sampleCount; sampleIndex++) {

			var waveformSample = waveform.getSample(periodPercent);
			var waveSample = fma(amplitude, (waveformSample - LOWER_PEAK) / AMPLITUDE, lowerPeak);

			samples[sampleIndex] = waveSample;

			var modulationSample = modulationSamples == null ? 0.0 : modulationSamples[sampleIndex];
			var modulationFactor = pow(2.0, octave + modulationSample);

			periodPercent = fma(frequency, modulationFactor / sampleRate, periodPercent) % 1.0;
		}

		return samples;
	}
}
package com.github.achaaab.bragi.module;

import com.github.achaaab.bragi.common.Settings;
import org.slf4j.Logger;

import static java.lang.Math.exp;
import static java.lang.Math.pow;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Voltage-Controlled Filter with low-pass response
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.9
 */
public class LowPassVCF extends VCF {

	private static final Logger LOGGER = getLogger(LowPassVCF.class);

	public static final String DEFAULT_NAME = "low_pass_vcf";

	private static final double VOLTS_PER_OCTAVE = 5.0;

	/**
	 * Creates a low-pass VCF with default name.
	 *
	 * @see #DEFAULT_NAME
	 * @since 0.0.9
	 */
	public LowPassVCF() {
		this(DEFAULT_NAME);
	}

	/**
	 * @param name name of the low-pass filter
	 */
	public LowPassVCF(String name) {
		super(name);
	}

	@Override
	protected void filterSamples() {

		var sampleCount = inputSamples.length;
		var sampleRate = Settings.INSTANCE.getFrameRate();

		outputSamples = new float[sampleCount];

		for (var sampleIndex = 0; sampleIndex < sampleCount; sampleIndex++) {

			if (modulationSamples == null) {

				actualCutOffFrequency = cutOffFrequency;

			} else {

				modulationSample = modulationSamples[sampleIndex];
				actualCutOffFrequency = cutOffFrequency * pow(2.0, modulationSample / VOLTS_PER_OCTAVE);
			}

			var f = 2 * actualCutOffFrequency / sampleRate;

			// empirical tuning
			var k = 3.6 * f - 1.6 * f * f - 1;
			var p = (k + 1) * 0.5;
			var scale = exp((1 - p) * 1.386249);
			var r = emphasis * scale;

			var inputSample = inputSamples[sampleIndex] / 5 - r * y4;

			// four cascaded one-pole filters (bilinear transform)
			y1 = inputSample * p + oldX * p - k * y1;
			y2 = y1 * p + oldY1 * p - k * y2;
			y3 = y2 * p + oldY2 * p - k * y3;
			y4 = y3 * p + oldY3 * p - k * y4;

			// clipper band limited sigmoid
			y4 = y4 - (y4 * y4 * y4) / 6;

			oldX = inputSample;
			oldY1 = y1;
			oldY2 = y2;
			oldY3 = y3;

			outputSamples[sampleIndex] = (float) (y4 * 5);
		}
	}
}
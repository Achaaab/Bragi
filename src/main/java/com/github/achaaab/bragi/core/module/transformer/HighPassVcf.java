package com.github.achaaab.bragi.core.module.transformer;

import com.github.achaaab.bragi.common.Settings;
import org.slf4j.Logger;

import static java.lang.Math.exp;
import static java.lang.Math.fma;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.pow;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Voltage-Controlled Filter with high-pass response
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.9
 */
public class HighPassVcf extends Vcf {

	private static final Logger LOGGER = getLogger(HighPassVcf.class);

	public static final String DEFAULT_NAME = "high_pass_vcf";

	/**
	 * Creates a high-pass VCF with default name.
	 *
	 * @see #DEFAULT_NAME
	 * @since 0.0.9
	 */
	public HighPassVcf() {
		this(DEFAULT_NAME);
	}

	/**
	 * @param name name of the high-pass filter
	 * @since 0.2.0
	 */
	public HighPassVcf(String name) {
		super(name);
	}

	@Override
	protected void filterSamples() {

		var sampleCount = inputSamples.length;
		var nyquistFrequency = Settings.INSTANCE.nyquistFrequency();

		outputSamples = new float[sampleCount];

		for (var sampleIndex = 0; sampleIndex < sampleCount; sampleIndex++) {

			if (modulationSamples == null) {

				actualCutoffFrequency = cutoffFrequency;

			} else {

				modulationSample = modulationSamples[sampleIndex];
				actualCutoffFrequency = cutoffFrequency * pow(2.0, modulationSample);
			}

			var f = min(1.0, actualCutoffFrequency / nyquistFrequency);

			// empirical tuning
			var k = fma(f, 3.6 - 1.6 * f, -1);
			var p = fma(0.5f, k, 0.5f);
			var exponent = fma(-1.386249, p, 1.386249);
			var scale = exp(exponent);
			var r = emphasis * scale;

			// inverted feedback for corner peaking (emphasis)
			var x = NORMALIZER.normalize(inputSamples[sampleIndex]) - r * y4;

			// four cascaded one-pole filters (bilinear transform)
			y1 = fma(p, x + oldX, -k * y1);
			y2 = fma(p, y1 + oldY1, -k * y2);
			y3 = fma(p, y2 + oldY2, -k * y3);
			y4 = fma(p, y3 + oldY3, -k * y4);

			// clipper band limited sigmoid
			y4 = fma(-y4 * y4, y4 / 6, y4);

			oldX = x;
			oldY1 = y1;
			oldY2 = y2;
			oldY3 = y3;

			/*
			With high cutoff frequency and high emphasis, typically 10Khz and 100% we have an issue with
			float overflow, starting with y4. To prevent this, we keep y4 in [-1.5f, 1.5f]. It does not seem
			to deteriorate the filter.
			 */

			y4 = min(1.5f, max(-1.5f, y4));

			outputSamples[sampleIndex] = NORMALIZER.inverseNormalize(oldX - y4);
		}
	}
}

package fr.guehenneux.bragi.module;

import fr.guehenneux.bragi.common.Settings;

import static java.lang.Math.exp;
import static java.lang.Math.pow;

/**
 * Voltage-Controlled Filter with low-pass response
 *
 * @author Jonathan Guéhenneux
 */
public class LowPassVCF extends VCF {

	private static final double VOLT_PER_OCTAVE = 5.0;

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
				actualCutOffFrequency = cutOffFrequency * pow(2.0, modulationSample / VOLT_PER_OCTAVE);
			}

			// f ∈ [0, 1]
			var f = 2 * actualCutOffFrequency / sampleRate;

			// empirical tuning

			// k ∈ [-1, 1]
			var k = 3.6 * f - 1.6 * f * f - 1;

			// p ∈ [0, 1]
			var p = (k + 1) * 0.5;

			// scale ∈ [1, 4]
			var scale = exp((1 - p) * 1.386249);

			// r ∈ [0, 4]
			var r = emphasis * scale;

			var inputSample = inputSamples[sampleIndex] / 5 - r * y4;

			// four cascaded one-pole filters (bilinear transform)
			y1 = inputSample * p + oldx * p - k * y1;
			y2 = y1 * p + oldy1 * p - k * y2;
			y3 = y2 * p + oldy2 * p - k * y3;
			y4 = y3 * p + oldy3 * p - k * y4;

			// clipper band limited sigmoid
			y4 = y4 - (y4 * y4 * y4) / 6;

			oldx = inputSample;
			oldy1 = y1;
			oldy2 = y2;
			oldy3 = y3;

			outputSamples[sampleIndex] = (float) (y4 * 5);
		}
	}
}
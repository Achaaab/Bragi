package fr.guehenneux.bragi.module;

import fr.guehenneux.bragi.Settings;

/**
 * @author Jonathan Guéhenneux
 */
public class HighPassVCF extends VCF {

	/**
	 * @param name
	 */
	public HighPassVCF(String name) {
		super(name);
	}

	@Override
	protected void filterSamples() {

		int sampleCount = inputSamples.length;
		outputSamples = new float[sampleCount];
		float sampleRate = Settings.INSTANCE.getFrameRate();

		float inputSample;

		for (int sampleIndex = 0; sampleIndex < sampleCount; sampleIndex++) {

			if (modulation) {

				modulationSample = modulationSamples[sampleIndex];
				actualCutOffFrequency = (float) (cutOffFrequency * Math.pow(2.0, modulationSample));

			} else {

				actualCutOffFrequency = cutOffFrequency;
			}

			float f = 2 * (float) actualCutOffFrequency / sampleRate;

			/*
			 * empirical tuning
			 */

			float k = 3.6f * f - 1.6f * f * f - 1;
			float p = (k + 1) * 0.5f;
			float scale = (float) Math.exp((1 - p) * 1.386249);
			float r = rezLevel / 100 * scale;

			inputSample = inputSamples[sampleIndex] - r * y4;

			/*
			 * four cascaded one-pole filters (bilinear transform)
			 */

			y1 = inputSample * p + oldx * p - k * y1;
			y2 = y1 * p + oldy1 * p - k * y2;
			y3 = y2 * p + oldy2 * p - k * y3;
			y4 = y3 * p + oldy3 * p - k * y4;

			/*
			 * clipper band limited sigmoid
			 */

			y4 = y4 - (y4 * y4 * y4) / 6;

			oldx = inputSample;
			oldy1 = y1;
			oldy2 = y2;
			oldy3 = y3;

			outputSamples[sampleIndex] = oldx - y4;
		}
	}
}
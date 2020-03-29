package com.github.achaaab.bragi.module;

import com.github.achaaab.bragi.common.connection.Output;
import com.github.achaaab.bragi.common.connection.Input;

import static java.lang.Math.round;

/**
 * @author Jonathan Guéhenneux
 */
public class Sampler extends Module {

	private static final int SAMPLE_SIZE = 64;
	private static final int FRAME_RATE_DIVISOR = 4;

	private Input input;
	private Output output;

	/**
	 * @param name sampler name
	 */
	public Sampler(String name) {

		super(name);

		input = addPrimaryInput(name + "_input");
		output = addPrimaryOutput(name + "_output");

		start();
	}

	@Override
	public int compute() throws InterruptedException {

		var inputSamples = input.read();
		var sampleCount = inputSamples.length;

		var outputSamples = new float[sampleCount];

		var sampleSum = 0.0f;
		var groupSize = 0;

		for (var sampleIndex = 0; sampleIndex < sampleCount; ) {

			sampleSum += inputSamples[sampleIndex++];
			groupSize++;

			if (groupSize == FRAME_RATE_DIVISOR || sampleIndex == sampleCount) {

				var sampleMedium = sampleSum / groupSize;
				sampleMedium = (float) round(sampleMedium * SAMPLE_SIZE) / SAMPLE_SIZE;

				for (int i = 0; i < groupSize; i++) {
					outputSamples[sampleIndex - i - 1] = sampleMedium;
				}

				groupSize = 0;
				sampleSum = 0;
			}
		}

		output.write(outputSamples);

		return sampleCount;
	}
}
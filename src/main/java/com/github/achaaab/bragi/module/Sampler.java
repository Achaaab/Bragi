package com.github.achaaab.bragi.module;

import com.github.achaaab.bragi.common.connection.Input;
import com.github.achaaab.bragi.common.connection.Output;
import org.slf4j.Logger;

import static java.lang.Math.round;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * sampler
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.9
 */
public class Sampler extends Module {

	private static final Logger LOGGER = getLogger(Sampler.class);

	public static final String DEFAULT_NAME = "sampler";

	private static final int SAMPLE_SIZE = 64;
	private static final int FRAME_RATE_DIVISOR = 4;

	private final Input input;
	private final Output output;

	/**
	 * Creates a sampler with default name.
	 *
	 * @see #DEFAULT_NAME
	 * @since 0.0.9
	 */
	public Sampler() {
		this(DEFAULT_NAME);
	}

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
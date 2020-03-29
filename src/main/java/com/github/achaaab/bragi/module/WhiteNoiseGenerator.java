package com.github.achaaab.bragi.module;

import com.github.achaaab.bragi.common.Normalizer;
import com.github.achaaab.bragi.common.Settings;
import com.github.achaaab.bragi.common.connection.Output;

import java.util.Random;

/**
 * White noise generator.
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.7
 */
public class WhiteNoiseGenerator extends Module {

	private Output output;

	private Random random;
	private Normalizer normalizer;

	/**
	 * @param name white noise generator name
	 */
	public WhiteNoiseGenerator(String name) {

		super(name);

		output = addPrimaryOutput(name + "_output");

		random = new Random();

		normalizer = new Normalizer(0.0f, 1.0f,
				Settings.INSTANCE.getMinimalVoltage(),
				Settings.INSTANCE.getMaximalVoltage());

		start();
	}

	@Override
	protected int compute() throws InterruptedException {

		var sampleCount = Settings.INSTANCE.getChunkSize();
		var outputSamples = new float[sampleCount];

		for (var sampleIndex = 0; sampleIndex < sampleCount; sampleIndex++) {
			outputSamples[sampleIndex] = normalizer.normalize(random.nextFloat());
		}

		output.write(outputSamples);

		return sampleCount;
	}
}
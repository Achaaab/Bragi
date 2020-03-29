package com.github.achaaab.bragi.module;

import com.github.achaaab.bragi.common.Normalizer;
import com.github.achaaab.bragi.common.Settings;
import com.github.achaaab.bragi.common.connection.Output;
import org.slf4j.Logger;

import java.util.concurrent.ThreadLocalRandom;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * White noise generator.
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.7
 */
public class WhiteNoiseGenerator extends Module {

	private static final Logger LOGGER = getLogger(WhiteNoiseGenerator.class);

	public static final String DEFAULT_NAME = "white_noise_generator";

	private Output output;

	private Normalizer normalizer;

	/**
	 * Creates a white noise generator with default name.
	 *
	 * @see #DEFAULT_NAME
	 * @since 0.0.9
	 */
	public WhiteNoiseGenerator() {
		this(DEFAULT_NAME);
	}

	/**
	 * @param name white noise generator name
	 */
	public WhiteNoiseGenerator(String name) {

		super(name);

		output = addPrimaryOutput(name + "_output");

		normalizer = new Normalizer(0.0f, 1.0f,
				Settings.INSTANCE.getMinimalVoltage(),
				Settings.INSTANCE.getMaximalVoltage());

		start();
	}

	@Override
	protected int compute() throws InterruptedException {

		var sampleCount = Settings.INSTANCE.getChunkSize();
		var samples = new float[sampleCount];

		var random = ThreadLocalRandom.current();

		for (var sampleIndex = 0; sampleIndex < sampleCount; sampleIndex++) {
			samples[sampleIndex] = normalizer.normalize(random.nextFloat());
		}

		output.write(samples);

		return sampleCount;
	}
}
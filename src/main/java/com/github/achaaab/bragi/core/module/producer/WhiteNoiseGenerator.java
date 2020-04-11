package com.github.achaaab.bragi.core.module.producer;

import com.github.achaaab.bragi.common.Normalizer;
import com.github.achaaab.bragi.common.Settings;
import com.github.achaaab.bragi.core.connection.Output;
import com.github.achaaab.bragi.core.module.Module;
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

	private final Output output;

	private static final Normalizer NORMALIZER = new Normalizer(
			0.0f, 1.0f,
			Settings.INSTANCE.minimalVoltage(),
			Settings.INSTANCE.maximalVoltage());

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
	}

	@Override
	protected int compute() throws InterruptedException {

		var sampleCount = Settings.INSTANCE.chunkSize();
		var samples = new float[sampleCount];

		var random = ThreadLocalRandom.current();

		for (var sampleIndex = 0; sampleIndex < sampleCount; sampleIndex++) {
			samples[sampleIndex] = NORMALIZER.normalize(random.nextFloat());
		}

		output.write(samples);

		return sampleCount;
	}
}
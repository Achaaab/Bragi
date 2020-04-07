package com.github.achaaab.bragi.core.module.transformer;

import com.github.achaaab.bragi.core.connection.Input;
import com.github.achaaab.bragi.core.connection.Output;
import com.github.achaaab.bragi.core.module.Module;
import org.slf4j.Logger;

import java.util.concurrent.ThreadLocalRandom;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Jonathan Guéhenneux
 * @since 0.0.9
 */
public class Ditherer extends Module {

	private static final Logger LOGGER = getLogger(Ditherer.class);

	public static final String DEFAULT_NAME = "ditherer";

	private final Input input;
	private final Output output;

	/**
	 * Creates a ditherer with default name.
	 *
	 * @see #DEFAULT_NAME
	 * @since 0.0.9
	 */
	public Ditherer() {
		this(DEFAULT_NAME);
	}

	/**
	 * @param name ditherer name
	 */
	public Ditherer(String name) {

		super(name);

		input = addPrimaryInput(name + "_input");
		output = addPrimaryOutput(name + "_output");

		start();
	}

	@Override
	protected int compute() throws InterruptedException {

		var inputSamples = input.read();
		var sampleCount = inputSamples.length;

		var random = ThreadLocalRandom.current();

		var outputSamples = new float[sampleCount];

		for (var sampleIndex = 0; sampleIndex < sampleCount; sampleIndex++) {
			outputSamples[sampleIndex] = inputSamples[sampleIndex] + random.nextFloat() / 100;
		}

		output.write(outputSamples);

		return sampleCount;
	}
}
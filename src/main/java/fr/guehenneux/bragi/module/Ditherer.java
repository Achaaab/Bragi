package fr.guehenneux.bragi.module;

import fr.guehenneux.bragi.common.connection.PrimaryInput;
import fr.guehenneux.bragi.common.connection.Output;

import static java.lang.Math.random;

/**
 * @author Jonathan Guéhenneux
 */
public class Ditherer extends Module {

	private PrimaryInput input;
	private Output output;

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

		var outputSamples = new float[sampleCount];

		for (int sampleIndex = 0; sampleIndex < sampleCount; sampleIndex++) {
			outputSamples[sampleIndex] = inputSamples[sampleIndex] + (float) random() / 100;
		}

		output.write(outputSamples);

		return sampleCount;
	}
}
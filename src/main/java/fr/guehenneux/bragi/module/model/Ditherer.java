package fr.guehenneux.bragi.module.model;

import fr.guehenneux.bragi.connection.PrimaryInput;
import fr.guehenneux.bragi.connection.Output;

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
		output = addOutput(name + "_output");

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
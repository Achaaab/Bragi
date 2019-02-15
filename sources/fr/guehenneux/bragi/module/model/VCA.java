package fr.guehenneux.bragi.module.model;

import fr.guehenneux.bragi.connection.Input;
import fr.guehenneux.bragi.connection.Output;

/**
 * Voltage-Controlled Amplifier
 *
 * @author Jonathan Guéhenneux
 */
public class VCA extends Module {

	private Input input;
	private Input gain;
	private Output output;

	/**
	 * @param name VCA name
	 */
	public VCA(String name) {

		super(name);

		input = addInput(name + "_input");
		gain = addInput(name + "_gain");
		output = addOutput(name + "_output");

		start();
	}

	@Override
	public void compute() throws InterruptedException {

		float[] inputSamples = input.read();
		float[] gainSamples = gain.read();

		int sampleCount = inputSamples.length;
		float[] outputSamples = new float[sampleCount];

		float inputSample;
		float gainSample;
		float outputSample;

		for (int sampleIndex = 0; sampleIndex < sampleCount; sampleIndex++) {

			inputSample = inputSamples[sampleIndex];
			gainSample = gainSamples[sampleIndex];
			outputSample = (float) (inputSample * Math.pow(2, 4 * (gainSample - 1)));
			outputSamples[sampleIndex] = outputSample;
		}

		output.write(outputSamples);
	}

	/**
	 * @return gain
	 */
	public Input getGain() {
		return gain;
	}
}
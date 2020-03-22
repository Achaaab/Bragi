package fr.guehenneux.bragi.module.model;

import fr.guehenneux.bragi.connection.Input;
import fr.guehenneux.bragi.connection.Output;

/**
 * @author Jonathan Guéhenneux
 */
public class Sampler extends Module {

	private static final int SAMPLE_SIZE = 63;
	private static final int FRAME_RATE_DIVISOR = 1;

	private Input input;
	private Output output;

	/**
	 * @param name sampler name
	 */
	public Sampler(String name) {

		super(name);

		input = addInput(name + "_input");
		output = addOutput(name + "_output");

		start();
	}

	@Override
	public void compute() throws InterruptedException {

		float[] inputSamples = input.read();
		int sampleCount = inputSamples.length;
		float[] outputSamples = new float[sampleCount];

		float sampleSum = 0, sampleMedium;
		int groupSize = 0;

		for (int sampleIndex = 0; sampleIndex < sampleCount; ) {

			sampleSum += inputSamples[sampleIndex++];
			groupSize++;

			if (groupSize == FRAME_RATE_DIVISOR || sampleIndex == sampleCount) {

				sampleMedium = sampleSum / groupSize;
				sampleMedium = (float) Math.round(sampleMedium * SAMPLE_SIZE) / SAMPLE_SIZE;

				for (int i = 0; i < groupSize; i++) {
					outputSamples[sampleIndex - i - 1] = sampleMedium;
				}

				groupSize = 0;
				sampleSum = 0;
			}
		}

		output.write(outputSamples);
	}
}
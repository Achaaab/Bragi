package fr.guehenneux.bragi.module;

/**
 * @author Jonathan Guéhenneux
 */
public class Sampler extends Module {

	private static final int SAMPLE_SIZE = 63;
	private static final int FRAME_RATE_DIVISOR = 8;

	protected Input input;
	protected Output output;
	private float[] samples;

	/**
	 * @param name
	 */
	public Sampler(String name) {

		super(name);

		input = addInput(name + "_input");
		output = addOutput(name + "_output");

		start();
	}

	@Override
	public void compute() throws InterruptedException {

		samples = input.read();

		int sampleCount = samples.length;
		float sampleSum = 0, sampleMedium;
		int groupSize = 0;

		for (int sampleIndex = 0; sampleIndex < sampleCount; ) {

			sampleSum += samples[sampleIndex++];
			groupSize++;

			if (groupSize == FRAME_RATE_DIVISOR || sampleIndex == sampleCount) {

				sampleMedium = sampleSum / groupSize;
				sampleMedium = (float) Math.round(sampleMedium * SAMPLE_SIZE) / SAMPLE_SIZE;

				for (int i = 0; i < groupSize; i++) {
					samples[sampleIndex - i - 1] = sampleMedium;
				}

				groupSize = 0;
				sampleSum = 0;
			}
		}

		output.write(samples);
	}
}
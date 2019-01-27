package fr.guehenneux.bragi.module;

/**
 * @author Jonathan GUEHENNEUX
 */
public class Sampler extends Module {

	private static final int SAMPLE_SIZE = 256;
	private static final int FRAME_RATE_DIVISOR = 2;

	protected Input input;
	protected Output output;
	private float[] samples;

	/**
	 *
	 * @param name
	 */
	public Sampler(String name) {

		super(name);

		input = new Input();
		output = new Output();
	}

	@Override
	public void compute() throws InterruptedException {

		if (output.isConnected() && input.isConnected()) {

			samples = input.read();

			int sampleCount = samples.length;
			float sampleSum = 0, sampleMedium;
			int groupSize = 0;

			for (int sampleIndex = 0; sampleIndex < sampleCount;) {

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

	/**
	 * @return the input
	 */
	public Input getInput() {
		return input;
	}

	/**
	 * @return the output
	 */
	public Output getOutput() {
		return output;
	}
}
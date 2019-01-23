package fr.guehenneux.audio;

/**
 * @author Jonathan GUEHENNEUX
 */
public class Sampler extends Module {

	private static final int SAMPLE_SIZE = 256;
	private static final int FRAME_RATE_DIVISOR = 2;

	protected InputPort inputPort;
	protected OutputPort outputPort;
	private float[] samples;

	/**
	 *
	 * @param name
	 */
	public Sampler(String name) {

		super(name);

		inputPort = new InputPort();
		outputPort = new OutputPort();
	}

	@Override
	public void compute() throws InterruptedException {

		if (outputPort.isConnected() && inputPort.isConnected()) {

			samples = inputPort.read();

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

			outputPort.write(samples);
		}
	}

	/**
	 * @return the inputPort
	 */
	public InputPort getInputPort() {
		return inputPort;
	}

	/**
	 * @return the outputPort
	 */
	public OutputPort getOutputPort() {
		return outputPort;
	}
}
package fr.guehenneux.audio;

/**
 * @author Jonathan Guéhenneux
 */
public class LFO extends Module {

	private OutputPort outputPort;

	private Wave wave;

	/**
	 * @param frequency
	 */
	public LFO(String name, double frequency) {

		super(name);

		outputPort = new OutputPort();
		wave = new SawtoothWave(frequency);
		new PresentationLFO(this);
	}

	/**
	 * @return
	 */
	public double getFrequency() {
		return wave.getFrequency();
	}

	/**
	 * @param frequency
	 */
	public void setFrequency(double frequency) {
		wave.setFrequency(frequency);
	}

	@Override
	public void compute() throws InterruptedException {

		if (outputPort.isConnected()) {

			int sampleCount = Settings.INSTANCE.getBufferSizeInFrames();
			double sampleLength = Settings.INSTANCE.getFrameLength();

			float[] samples = wave.getSamples(sampleCount, sampleLength);

			outputPort.write(samples);
		}
	}

	/**
	 * @return the output port
	 */
	public OutputPort getOutputPort() {
		return outputPort;
	}

	/**
	 * @param wave
	 */
	public void setWave(Wave wave) {
		this.wave = wave;
	}
}
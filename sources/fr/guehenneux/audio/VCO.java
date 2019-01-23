package fr.guehenneux.audio;

/**
 * @author Jonathan Guéhenneux
 */
public class VCO extends Module {

	private InputPort modulationPort;
	private OutputPort outputPort;

	private Wave wave;

	/**
	 * @param name
	 * @param frequency
	 */
	public VCO(String name, double frequency) {

		super(name);

		modulationPort = new InputPort();
		outputPort = new OutputPort();
		wave = new SquareWave(frequency);
		new PresentationVCO(this);
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

			float[] samples;

			if (modulationPort.isConnected()) {

				float[] modulationSamples = modulationPort.read();
				samples = wave.getSamples(modulationSamples, sampleCount, sampleLength);

			} else {

				samples = wave.getSamples(sampleCount, sampleLength);
			}

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
	 * @return the modulation prot
	 */
	public InputPort getModulationPort() {
		return modulationPort;
	}

	/**
	 * @param wave
	 */
	public void setWave(Wave wave) {
		this.wave = wave;
	}
}
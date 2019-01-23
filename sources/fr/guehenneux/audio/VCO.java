package fr.guehenneux.audio;

public class VCO extends Module {

	private InputPort modulationPort;

	private OutputPort outputPort;

	private Wave wave;

	/**
	 * 
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
	public void compute() {

		if (outputPort.isConnected()) {

			FormatAudio formatAudio = FormatAudio.getInstance();
			int sampleCount = formatAudio.getBufferSizeInFrames();
			double sampleLength = formatAudio.getFrameLength();

			float[] samples;

			if (modulationPort.isConnected() && modulationPort.isReady()) {

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
	 *            wave à définir
	 */
	public void setWave(Wave wave) {
		this.wave = wave;
	}
}
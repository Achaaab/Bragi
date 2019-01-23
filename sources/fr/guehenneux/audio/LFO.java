package fr.guehenneux.audio;

public class LFO extends Module {

	private OutputPort outputPort;

	private Wave wave;

	/**
	 * @param frequency
	 */
	public LFO(String name, double frequency) {

		super(name);

		outputPort = new OutputPort();
		wave = new SineWave(frequency);
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
	public void compute() {

		if (outputPort.isConnected()) {

			FormatAudio formatAudio = FormatAudio.getInstance();
			int sampleCount = formatAudio.getBufferSizeInFrames();
			double sampleLength = formatAudio.getFrameLength();

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
	 *            wave à définir
	 */
	public void setWave(Wave wave) {
		this.wave = wave;
	}
}

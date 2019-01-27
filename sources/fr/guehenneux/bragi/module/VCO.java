package fr.guehenneux.bragi.module;

import fr.guehenneux.bragi.Settings;
import fr.guehenneux.bragi.wave.SquareWave;
import fr.guehenneux.bragi.wave.Wave;

/**
 * @author Jonathan Guéhenneux
 */
public class VCO extends Module {

	private Input modulationPort;
	private Output output;

	private Wave wave;

	/**
	 * @param name
	 * @param frequency
	 */
	public VCO(String name, double frequency) {

		super(name);

		modulationPort = new Input();
		output = new Output();
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

		if (output.isConnected()) {

			int sampleCount = Settings.INSTANCE.getBufferSizeInFrames();
			double sampleLength = Settings.INSTANCE.getFrameLength();

			float[] samples;

			if (modulationPort.isConnected()) {

				float[] modulationSamples = modulationPort.read();
				samples = wave.getSamples(modulationSamples, sampleCount, sampleLength);

			} else {

				samples = wave.getSamples(sampleCount, sampleLength);
			}

			output.write(samples);
		}
	}

	/**
	 * @return the output port
	 */
	public Output getOutput() {
		return output;
	}

	/**
	 * @return the modulation prot
	 */
	public Input getModulationPort() {
		return modulationPort;
	}

	/**
	 * @param wave
	 */
	public void setWave(Wave wave) {
		this.wave = wave;
	}
}
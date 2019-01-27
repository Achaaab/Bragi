package fr.guehenneux.bragi.module;

import fr.guehenneux.bragi.wave.SawtoothWave;
import fr.guehenneux.bragi.Settings;
import fr.guehenneux.bragi.wave.Wave;

/**
 * @author Jonathan Guéhenneux
 */
public class LFO extends Module {

	private Output output;

	private Wave wave;

	/**
	 * @param frequency
	 */
	public LFO(String name, double frequency) {

		super(name);

		output = new Output();
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

		if (output.isConnected()) {

			int sampleCount = Settings.INSTANCE.getBufferSizeInFrames();
			double sampleLength = Settings.INSTANCE.getFrameLength();

			float[] samples = wave.getSamples(sampleCount, sampleLength);

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
	 * @param wave
	 */
	public void setWave(Wave wave) {
		this.wave = wave;
	}
}
package fr.guehenneux.bragi.module;

import fr.guehenneux.bragi.wave.SawtoothWave;
import fr.guehenneux.bragi.Settings;
import fr.guehenneux.bragi.wave.Wave;

/**
 * Low Frequency Oscillator
 *
 * @author Jonathan Guéhenneux
 */
public class LFO extends Module {

	private Output output;
	private Wave wave;

	/**
	 * @param name
	 * @param frequency
	 */
	public LFO(String name, double frequency) {

		super(name);

		output = addOutput(name + "_output");
		wave = new SawtoothWave(frequency);

		new LFOView(this);

		start();
	}

	@Override
	public void compute() throws InterruptedException {

		int sampleCount = Settings.INSTANCE.getBufferSizeInFrames();
		double sampleLength = Settings.INSTANCE.getFrameLength();

		float[] samples = wave.getSamples(sampleCount, sampleLength);
		output.write(samples);
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

	/**
	 * @param wave
	 */
	public void setWave(Wave wave) {
		this.wave = wave;
	}
}
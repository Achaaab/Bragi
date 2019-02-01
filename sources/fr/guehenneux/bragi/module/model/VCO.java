package fr.guehenneux.bragi.module.model;

import fr.guehenneux.bragi.Settings;
import fr.guehenneux.bragi.connection.Input;
import fr.guehenneux.bragi.connection.Output;
import fr.guehenneux.bragi.module.view.VCOView;
import fr.guehenneux.bragi.wave.Pulse;
import fr.guehenneux.bragi.wave.Wave;

/**
 * Voltage-Controlled Oscillator
 *
 * @author Jonathan Guéhenneux
 */
public class VCO extends Module {

	private Input modulation;
	private Output output;

	private Wave wave;

	/**
	 * @param name
	 * @param frequency
	 */
	public VCO(String name, double frequency) {

		super(name);

		modulation = addInput(name + "_modulation");
		output = addOutput(name + "_output");

		wave = new Wave(Pulse.PULSE_25, frequency);

		new VCOView(this);

		start();
	}

	@Override
	public void compute() throws InterruptedException {

		int sampleCount = Settings.INSTANCE.getBufferSizeInFrames();
		double sampleLength = Settings.INSTANCE.getFrameLength();

		float[] samples;

		if (modulation.isConnected()) {

			float[] modulationSamples = modulation.read();
			samples = wave.getSamples(modulationSamples, sampleCount, sampleLength);

		} else {

			samples = wave.getSamples(sampleCount, sampleLength);
		}

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
	 * @return the modulation port
	 */
	public Input getModulation() {
		return modulation;
	}

	/**
	 * @param wave
	 */
	public void setWave(Wave wave) {
		this.wave = wave;
	}
}
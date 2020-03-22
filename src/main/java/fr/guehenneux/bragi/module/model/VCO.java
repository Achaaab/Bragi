package fr.guehenneux.bragi.module.model;

import fr.guehenneux.bragi.Settings;
import fr.guehenneux.bragi.connection.Input;
import fr.guehenneux.bragi.connection.Output;
import fr.guehenneux.bragi.module.view.VCOView;
import fr.guehenneux.bragi.wave.Pulse;
import fr.guehenneux.bragi.wave.Sine;
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
	 * @param name name of the VCO
	 * @param frequency initial frequency of the VCO in hertz
	 */
	public VCO(String name, double frequency) {

		super(name);

		modulation = addInput(name + "_modulation");
		output = addOutput(name + "_output");

		wave = new Wave(Sine.INSTANCE, frequency);

		new VCOView(this);

		start();
	}

	@Override
	public int compute() throws InterruptedException {

		var sampleCount = Settings.INSTANCE.getBufferSizeInFrames();
		var sampleLength = Settings.INSTANCE.getFrameLength();

		var modulationSamples = modulation.tryRead();
		var samples = wave.getSamples(modulationSamples, sampleCount, sampleLength);

		output.write(samples);

		return sampleCount;
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
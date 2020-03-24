package fr.guehenneux.bragi.module.model;

import fr.guehenneux.bragi.connection.Output;
import fr.guehenneux.bragi.module.view.LFOView;
import fr.guehenneux.bragi.Settings;
import fr.guehenneux.bragi.wave.Sine;
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
	 * @param name name of the LFO
	 * @param frequency initial frequency of the LFO in hertz
	 */
	public LFO(String name, double frequency) {

		super(name);

		output = addPrimaryOutput(name + "_output");

		wave = new Wave(Sine.INSTANCE, frequency);

		new LFOView(this);

		start();
	}

	@Override
	public int compute() throws InterruptedException {

		var sampleCount = Settings.INSTANCE.getChunkSize();
		var sampleLength = Settings.INSTANCE.getFrameLength();

		var samples = wave.getSamples(null, sampleCount, sampleLength);

		output.write(samples);

		return sampleCount;
	}

	/**
	 * @return frequency of the LFO in hertz
	 */
	public double getFrequency() {
		return wave.getFrequency();
	}

	/**
	 * @param frequency frequency of the LFO in hertz
	 */
	public void setFrequency(double frequency) {
		wave.setFrequency(frequency);
	}
}
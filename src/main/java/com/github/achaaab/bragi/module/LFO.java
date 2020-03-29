package com.github.achaaab.bragi.module;

import com.github.achaaab.bragi.common.connection.Output;
import com.github.achaaab.bragi.gui.module.LFOView;
import com.github.achaaab.bragi.common.Settings;
import com.github.achaaab.bragi.common.wave.Sine;
import com.github.achaaab.bragi.common.wave.Wave;
import com.github.achaaab.bragi.common.wave.Waveform;

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
	 */
	public LFO(String name) {

		super(name);

		output = addPrimaryOutput(name + "_output");

		wave = new Wave(Sine.INSTANCE, 3.2);

		new LFOView(this);

		start();
	}

	@Override
	public int compute() throws InterruptedException {

		var sampleCount = Settings.INSTANCE.getChunkSize();

		var samples = wave.getSamples(0, null, sampleCount);

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

	/**
	 * @return waveform of this LFO
	 */
	public Waveform getWaveform() {
		return wave.getWaveform();
	}

	/**
	 * @param waveform waveform to set
	 */
	public void setWaveform(Waveform waveform) {
		wave.setWaveform(waveform);
	}
}
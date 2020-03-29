package com.github.achaaab.bragi.module;

import com.github.achaaab.bragi.common.Settings;
import com.github.achaaab.bragi.common.connection.Input;
import com.github.achaaab.bragi.common.connection.Output;
import com.github.achaaab.bragi.gui.module.VCOView;
import com.github.achaaab.bragi.common.wave.Sine;
import com.github.achaaab.bragi.common.wave.Wave;
import com.github.achaaab.bragi.common.wave.Waveform;

/**
 * Voltage-Controlled Oscillator
 *
 * @author Jonathan Guéhenneux
 */
public class VCO extends Module {

	private static final double BASE_FREQUENCY = 441;

	private int octave;
	private float modulationWeight;

	private Input modulation;
	private Output output;

	private Wave wave;

	/**
	 * @param name name of the VCO
	 */
	public VCO(String name) {

		super(name);

		modulation = addSecondaryInput(name + "_modulation");
		output = addPrimaryOutput(name + "_output");

		octave = 0;
		modulationWeight = 1.0f;
		wave = new Wave(Sine.INSTANCE, BASE_FREQUENCY);

		new VCOView(this);

		start();
	}

	@Override
	public int compute() throws InterruptedException {

		var sampleCount = Settings.INSTANCE.getChunkSize();

		var modulationSamples = modulation.read();

		var samples = wave.getSamples(octave, modulationSamples, sampleCount);

		output.write(samples);

		return sampleCount;
	}

	/**
	 * @return modulation input
	 */
	public Input getModulation() {
		return modulation;
	}

	/**
	 * @return number of octaves to shift (in Octaves)
	 */
	public int getOctave() {
		return octave;
	}

	/**
	 * @param octave number of octaves to shift (in Octaves)
	 */
	public void setOctave(int octave) {
		this.octave = octave;
	}

	/**
	 * @param waveform waveform to set
	 */
	public void setWaveform(Waveform waveform) {
		wave.setWaveform(waveform);
	}
}
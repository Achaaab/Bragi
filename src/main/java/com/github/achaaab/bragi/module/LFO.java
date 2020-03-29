package com.github.achaaab.bragi.module;

import com.github.achaaab.bragi.common.Settings;
import com.github.achaaab.bragi.common.connection.Output;
import com.github.achaaab.bragi.common.wave.Sine;
import com.github.achaaab.bragi.common.wave.Wave;
import com.github.achaaab.bragi.common.wave.Waveform;
import com.github.achaaab.bragi.gui.module.LFOView;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Low Frequency Oscillator
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.9
 */
public class LFO extends Module {

	private static final Logger LOGGER = getLogger(LFO.class);

	public static final String DEFAULT_NAME = "lfo";
	public static final Waveform INITIAL_WAVEFORM = Sine.INSTANCE;
	public static final double INITIAL_FREQUENCY = 3.2;

	private Output output;
	private Wave wave;

	/**
	 * Creates an LFO with default name.
	 *
	 * @see #DEFAULT_NAME
	 * @since 0.0.9
	 */
	public LFO() {
		this(DEFAULT_NAME);
	}

	/**
	 * @param name name of the LFO
	 */
	public LFO(String name) {

		super(name);

		output = addPrimaryOutput(name + "_output");

		wave = new Wave(INITIAL_WAVEFORM, INITIAL_FREQUENCY);

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
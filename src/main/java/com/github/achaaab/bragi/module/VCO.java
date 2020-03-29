package com.github.achaaab.bragi.module;

import com.github.achaaab.bragi.common.Settings;
import com.github.achaaab.bragi.common.connection.Input;
import com.github.achaaab.bragi.common.connection.Output;
import com.github.achaaab.bragi.common.wave.Sine;
import com.github.achaaab.bragi.common.wave.Wave;
import com.github.achaaab.bragi.common.wave.Waveform;
import com.github.achaaab.bragi.gui.module.SpectrumAnalyzerView;
import com.github.achaaab.bragi.gui.module.VCOView;
import org.slf4j.Logger;

import static javax.swing.SwingUtilities.invokeLater;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Voltage-Controlled Oscillator
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.9
 */
public class VCO extends Module {

	private static final Logger LOGGER = getLogger(VCO.class);

	public static final String DEFAULT_NAME = "vco";

	private static final double BASE_FREQUENCY = 440;

	private int octave;

	private final Input modulation;
	private final Output output;

	private final Wave wave;

	/**
	 * Creates a VCO with default name.
	 *
	 * @see #DEFAULT_NAME
	 * @since 0.0.9
	 */
	public VCO() {
		this(DEFAULT_NAME);
	}

	/**
	 * @param name name of the VCO
	 */
	public VCO(String name) {

		super(name);

		modulation = addSecondaryInput(name + "_modulation");
		output = addPrimaryOutput(name + "_output");

		octave = 0;
		wave = new Wave(Sine.INSTANCE, BASE_FREQUENCY);

		invokeLater(() -> new VCOView(this));

		start();
	}

	@Override
	public int compute() throws InterruptedException {

		var sampleCount = Settings.INSTANCE.chunkSize();

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
	 * @return current waveform
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
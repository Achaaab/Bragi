package fr.guehenneux.bragi.module.model;

import fr.guehenneux.bragi.Settings;
import fr.guehenneux.bragi.connection.Input;
import fr.guehenneux.bragi.connection.Output;
import fr.guehenneux.bragi.module.view.VCOView;
import fr.guehenneux.bragi.wave.Sine;
import fr.guehenneux.bragi.wave.Wave;
import fr.guehenneux.bragi.wave.Waveform;

/**
 * Voltage-Controlled Oscillator
 *
 * @author Jonathan Guéhenneux
 */
public class VCO extends Module {

	private static final double BASE_FREQUENCY = 440.0;
	private int octave = 0;

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

		wave = new Wave(Sine.INSTANCE, BASE_FREQUENCY);

		new VCOView(this);

		start();
	}

	@Override
	public int compute() throws InterruptedException {

		var sampleCount = Settings.INSTANCE.getChunkSize();
		var sampleLength = Settings.INSTANCE.getFrameLength();

		var modulationSamples = modulation.read();

		var samples = wave.getSamples(octave, modulationSamples, sampleCount, sampleLength);

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
	 * @param octave octave to set
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
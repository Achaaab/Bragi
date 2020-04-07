package com.github.achaaab.bragi.core.module.producer;

import com.github.achaaab.bragi.core.connection.Output;
import com.github.achaaab.bragi.core.module.producer.wave.Wave;
import com.github.achaaab.bragi.core.module.producer.wave.Waveform;
import com.github.achaaab.bragi.core.module.Module;

/**
 * oscillator
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.3
 */
public abstract class Oscillator extends Module {

	protected final Output output;

	protected final Wave wave;

	/**
	 * @param name             name of the oscillator to create
	 * @param initialWaveform  initial waveform
	 * @param initialFrequency initial frequency in hertz
	 */
	public Oscillator(String name, Waveform initialWaveform, double initialFrequency) {

		super(name);

		output = addPrimaryOutput(name + "_output");

		wave = new Wave(initialWaveform, initialFrequency);
	}

	/**
	 * @return waveform of this oscillator
	 */
	public Waveform getWaveform() {
		return wave.getWaveform();
	}

	/**
	 * @param waveform waveform of this oscillator
	 */
	public void setWaveform(Waveform waveform) {
		wave.setWaveform(waveform);
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
	 * @return lower peak in volts
	 * @since 0.1.3
	 */
	public float getLowerPeak() {
		return wave.getLowerPeak();
	}

	/**
	 * @param lowerPeak lower peak in volts
	 * @since 0.1.3
	 */
	public void setLowerPeak(float lowerPeak) {
		wave.setLowerPeak(lowerPeak);
	}

	/**
	 * @return upper peak in volts
	 * @since 0.1.3
	 */
	public float getUpperPeak() {
		return wave.getUpperPeak();
	}

	/**
	 * @param upperPeak upper peak in volts
	 * @since 0.1.3
	 */
	public void setUpperPeak(float upperPeak) {
		wave.setUpperPeak(upperPeak);
	}
}
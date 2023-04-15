package com.github.achaaab.bragi.core.module.transformer;

import com.github.achaaab.bragi.core.connection.Input;
import com.github.achaaab.bragi.core.connection.Output;
import com.github.achaaab.bragi.core.module.Module;
import com.github.achaaab.bragi.core.module.ModuleCreationException;
import com.github.achaaab.bragi.gui.module.MixerView;
import org.slf4j.Logger;

import java.lang.reflect.InvocationTargetException;

import static java.lang.Math.pow;
import static javax.swing.SwingUtilities.invokeAndWait;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Jonathan Guéhenneux
 * @since 0.1.8
 */
public class Mixer extends Module {

	private static final Logger LOGGER = getLogger(Mixer.class);

	public static final String DEFAULT_NAME = "mixer";

	private final Input input0;
	private final Input input1;
	private final Output output;

	private int gain0;
	private int gain1;

	private float gainFactor0;
	private float gainFactor1;

	/**
	 * Creates a mixer with default name.
	 *
	 * @see #DEFAULT_NAME
	 * @since 0.2.0
	 */
	public Mixer() {
		this(DEFAULT_NAME);
	}

	/**
	 * Creates a mixer with the given name.
	 *
	 * @param name name of the mixer to create
	 * @since 0.2.0
	 */
	public Mixer(String name) {

		super(name);

		input0 = addPrimaryInput("input0");
		input1 = addSecondaryInput("input1");
		output = addPrimaryOutput("output");

		setGain0(-6);
		setGain1(-6);

		try {
			invokeAndWait(() -> view = new MixerView(this));
		} catch (InterruptedException | InvocationTargetException cause) {
			throw new ModuleCreationException(cause);
		}
	}

	@Override
	protected int compute() throws InterruptedException {

		var samples0 = input0.read();
		var samples1 = input1.read();

		var sampleCount = samples0.length;
		var samples = new float[sampleCount];

		for (var sampleIndex = 0; sampleIndex < sampleCount; sampleIndex++) {

			var sample0 = samples0[sampleIndex] * gainFactor0;
			var sample1 = samples1 == null ? 0 : samples1[sampleIndex] * gainFactor1;

			samples[sampleIndex] = sample0 + sample1;
		}

		output.write(samples);

		return sampleCount;
	}

	/**
	 * @return gain to apply to samples read from input 0 in decibels (dB)
	 * @since 0.2.0
	 */
	public int getGain0() {
		return gain0;
	}

	/**
	 * @param gain0 to apply to samples read from input 0 in decibels (dB)
	 * @since 0.2.0
	 */
	public void setGain0(int gain0) {

		this.gain0 = gain0;

		gainFactor0 = (float) pow(10.0, gain0 / 20.0);
	}

	/**
	 * @return gain to apply to samples read from input 1 in decibels (dB)
	 * @since 0.2.0
	 */
	public int getGain1() {
		return gain1;
	}

	/**
	 * @param gain1 to apply to samples read from input 1 in decibels (dB)
	 * @since 0.2.0
	 */
	public void setGain1(int gain1) {

		this.gain1 = gain1;

		gainFactor1 = (float) pow(10.0, gain1 / 20.0);
	}
}

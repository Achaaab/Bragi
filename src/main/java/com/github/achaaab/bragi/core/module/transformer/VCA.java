package com.github.achaaab.bragi.core.module.transformer;

import com.github.achaaab.bragi.core.connection.Input;
import com.github.achaaab.bragi.core.connection.Output;
import com.github.achaaab.bragi.core.module.Module;
import com.github.achaaab.bragi.core.module.ModuleCreationException;
import com.github.achaaab.bragi.gui.module.VCAView;
import org.slf4j.Logger;

import java.lang.reflect.InvocationTargetException;

import static java.lang.Math.pow;
import static javax.swing.SwingUtilities.invokeAndWait;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Voltage-Controlled Amplifier
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.9
 */
public class VCA extends Module {

	private static final Logger LOGGER = getLogger(VCA.class);

	public static final String DEFAULT_NAME = "vca";
	public static final float DECIBELS_PER_VOLT = 10.0f;

	private final Input input;
	private final Input gain;
	private final Output output;

	private int initialGain;

	/**
	 * Creates a VCA with default name.
	 *
	 * @see #DEFAULT_NAME
	 * @since 0.0.9
	 */
	public VCA() {
		this(DEFAULT_NAME);
	}

	/**
	 * @param name VCA name
	 */
	public VCA(String name) {

		super(name);

		input = addPrimaryInput(name + "_input");
		gain = addSecondaryInput(name + "_gain");
		output = addPrimaryOutput(name + "_output");

		initialGain = 0;

		try {
			invokeAndWait(() -> view = new VCAView(this));
		} catch (InterruptedException | InvocationTargetException cause) {
			throw new ModuleCreationException(cause);
		}
	}

	@Override
	public int compute() throws InterruptedException {

		var inputSamples = input.read();
		var sampleCount = inputSamples.length;
		var outputSamples = new float[sampleCount];

		var gainSamples = gain.read();

		for (var sampleIndex = 0; sampleIndex < sampleCount; sampleIndex++) {

			var inputSample = inputSamples[sampleIndex];
			var gainSample = gainSamples == null ? 0 : gainSamples[sampleIndex];
			var decibels = initialGain + DECIBELS_PER_VOLT * gainSample;

			var outputSample = (float) (inputSample * pow(10.0f, decibels / 20.0f));

			outputSamples[sampleIndex] = outputSample;
		}

		output.write(outputSamples);

		return sampleCount;
	}

	/**
	 * @return gain gain input
	 */
	public Input gain() {
		return gain;
	}

	/**
	 * @return initial gain in decibels
	 */
	public int getInitialGain() {
		return initialGain;
	}

	/**
	 * @param initialGain initial gain in decibels
	 */
	public void setInitialGain(int initialGain) {
		this.initialGain = initialGain;
	}
}
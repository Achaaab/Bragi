package fr.guehenneux.bragi.module;

import fr.guehenneux.bragi.common.connection.Input;
import fr.guehenneux.bragi.common.connection.Output;
import fr.guehenneux.bragi.gui.module.VCAView;
import org.slf4j.Logger;

import static java.lang.Math.pow;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Voltage-Controlled Amplifier
 *
 * @author Jonathan Guéhenneux
 */
public class VCA extends Module {

	private static final Logger LOGGER = getLogger(VCA.class);

	public static final float DECIBELS_PER_VOLT = 10.0f;

	private Input input;
	private Input gain;
	private Output output;

	private int initialGain;

	/**
	 * @param name VCA name
	 */
	public VCA(String name) {

		super(name);

		input = addPrimaryInput(name + "_input");
		gain = addSecondaryInput(name + "_gain");
		output = addPrimaryOutput(name + "_output");

		initialGain = 0;

		new VCAView(this);

		start();
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
	public Input getGain() {
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
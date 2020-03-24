package fr.guehenneux.bragi.module.model;

import fr.guehenneux.bragi.connection.Input;
import fr.guehenneux.bragi.connection.Output;
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

	private Input input;
	private Input gain;
	private Output output;

	/**
	 * @param name VCA name
	 */
	public VCA(String name) {

		super(name);

		input = addPrimaryInput(name + "_input");
		gain = addSecondaryInput(name + "_gain");
		output = addPrimaryOutput(name + "_output");

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
			var outputSample = (float) (inputSample * pow(2, 4 * (gainSample - 1)));

			outputSamples[sampleIndex] = outputSample;
		}

		output.write(outputSamples);

		return sampleCount;
	}

	/**
	 * @return gain
	 */
	public Input getGain() {
		return gain;
	}
}
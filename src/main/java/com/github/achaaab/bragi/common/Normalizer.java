package com.github.achaaab.bragi.common;

import static java.lang.Math.fma;
import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * A normalizer does an affine transform from an input range and an output range.
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.4
 */
public class Normalizer {

	private final float input0;
	private final float output0;

	private final float minimalOutput;
	private final float maximalOutput;

	private final float minimalInput;
	private final float maximalInput;

	private final float amplification;
	private final float inverseAmplification;

	/**
	 * @param input0 input sample 0
	 * @param input1 input sample 1
	 * @param output0 output sample 0
	 * @param output1 output sample 1
	 * @since 0.2.0
	 */
	public Normalizer(float input0, float input1, float output0, float output1) {

		this.input0 = input0;
		this.output0 = output0;

		var inputAmplitude = input1 - input0;
		var outputAmplitude = output1 - output0;

		amplification = outputAmplitude / inputAmplitude;
		inverseAmplification = inputAmplitude / outputAmplitude;

		minimalInput = min(input0, input1);
		maximalInput = max(input0, input1);

		minimalOutput = min(output0, output1);
		maximalOutput = max(output0, output1);
	}

	/**
	 * @param input input sample with double precision
	 * @return normalized sample
	 * @since 0.2.0
	 */
	public float normalize(double input) {
		return normalize((float) input);
	}

	/**
	 * @param output output sample with double precision
	 * @return inverse normalized sample
	 * @since 0.1.6
	 */
	public float inverseNormalize(double output) {
		return inverseNormalize((float) output);
	}

	/**
	 * @param input sample to normalize
	 * @return normalized sample
	 * @since 0.2.0
	 */
	public float normalize(float input) {

		var output = fma(amplification, input - input0, output0);
		return min(maximalOutput, max(minimalOutput, output));
	}

	/**
	 * @param output sample to inverse normalize
	 * @return inverse normalized sample
	 * @since 0.1.6
	 */
	public float inverseNormalize(float output) {

		var input = fma(inverseAmplification, output - output0, input0);
		return min(maximalInput, max(minimalInput, input));
	}
}

package com.github.achaaab.bragi.common;

import static java.lang.Math.fma;
import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * @author Jonathan Guéhenneux
 * @since 0.0.4
 */
public class Normalizer {

	private float input0;
	private float output0;
	private float minimalOutput;
	private float maximalOutput;

	private float amplification;

	/**
	 * @param input0  input sample 0
	 * @param input1  input sample 1
	 * @param output0 output sample 0
	 * @param output1 output sample 1
	 */
	public Normalizer(float input0, float input1, float output0, float output1) {

		this.input0 = input0;
		this.output0 = output0;

		var inputAmplitude = input1 - input0;
		var outputAmplitude = output1 - output0;

		amplification = outputAmplitude / inputAmplitude;
		minimalOutput = min(output0, output1);
		maximalOutput = max(output0, output1);
	}

	/**
	 * @param input input with double precision
	 * @return normalized sample
	 */
	public float normalize(double input) {
		return normalize((float) input);
	}

	/**
	 * @param input sample to normalize
	 * @return normalized sample
	 */
	public float normalize(float input) {

		var output = fma(amplification, input - input0, output0);
		return min(maximalOutput, max(minimalOutput, output));
	}
}
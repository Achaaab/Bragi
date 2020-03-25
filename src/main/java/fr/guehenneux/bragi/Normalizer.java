package fr.guehenneux.bragi;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * @author Jonathan Guéhenneux
 * @since 0.0.4
 */
public class Normalizer {

	private float minimalInput;
	private float maximalInput;
	private float minimalOutput;
	private float maximalOutput;

	private float inputAmplitude;
	private float outputAmplitude;
	private float amplification;

	/**
	 * @param minimalInput minimal input sample
	 * @param maximalInput maximal input sample
	 * @param minimalOutput minimal output sample
	 * @param maximalOutput maximal output sample
	 */
	public Normalizer(float minimalInput, float maximalInput, float minimalOutput, float maximalOutput) {

		this.minimalInput = minimalInput;
		this.maximalInput = maximalInput;
		this.minimalOutput = minimalOutput;
		this.maximalOutput = maximalOutput;

		inputAmplitude = maximalInput - minimalInput;
		outputAmplitude = maximalOutput - minimalOutput;
		amplification = outputAmplitude / inputAmplitude;
	}

	/**
	 * @param input sample to normalize
	 * @return normalized sample
	 */
	public float normalize(float input) {

		var output = minimalOutput + amplification * (input - minimalInput);
		return min(maximalOutput, max(minimalOutput, output));
	}
}
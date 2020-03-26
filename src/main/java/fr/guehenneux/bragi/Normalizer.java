package fr.guehenneux.bragi;

import static java.lang.Math.fma;
import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * @author Jonathan Guéhenneux
 * @since 0.0.4
 */
public class Normalizer {

	private float minimalInput;
	private float minimalOutput;
	private float maximalOutput;

	private float amplification;

	/**
	 * @param minimalInput minimal input sample
	 * @param maximalInput maximal input sample
	 * @param minimalOutput minimal output sample
	 * @param maximalOutput maximal output sample
	 */
	public Normalizer(float minimalInput, float maximalInput, float minimalOutput, float maximalOutput) {

		this.minimalInput = minimalInput;
		this.minimalOutput = minimalOutput;
		this.maximalOutput = maximalOutput;

		var inputAmplitude = maximalInput - minimalInput;
		var outputAmplitude = maximalOutput - minimalOutput;

		amplification = outputAmplitude / inputAmplitude;
	}

	/**
	 * @param input sample to normalize
	 * @return normalized sample
	 */
	public float normalize(float input) {

		var output = fma(amplification, input - minimalInput, minimalOutput);
		return min(maximalOutput, max(minimalOutput, output));
	}
}
package com.github.achaaab.bragi.common.fft;

/**
 * Window with memoized coefficients.
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.5
 */
public abstract class MemoizedWindow implements Window {

	private double[] coefficients;

	/**
	 * @param size number of coefficients to memoize
	 */
	private void memoizeCoefficients(int size) {

		coefficients = new double[size];

		for (var n = 0; n < size; n++) {
			coefficients[n] = getCoefficient(n, size);
		}
	}

	/**
	 * @param n sample index
	 * @param size number of samples
	 * @return coefficient to apply to sample at given index
	 */
	protected abstract double getCoefficient(int n, int size);

	@Override
	public void apply(float[] samples) {

		var size = samples.length;

		if (coefficients == null || coefficients.length != size) {
			memoizeCoefficients(size);
		}

		for (var n = 0; n < size; n++) {
			samples[n] *= coefficients[n];
		}
	}
}
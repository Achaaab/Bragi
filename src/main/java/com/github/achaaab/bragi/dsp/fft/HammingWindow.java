package com.github.achaaab.bragi.dsp.fft;

import static java.lang.Math.PI;
import static java.lang.Math.cos;

/**
 * Hamming window
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.5
 */
public class HammingWindow extends MemoizedWindow {

	public static final double DEFAULT_A0 = 0.53836;
	private static final double TWO_PI = PI * 2;

	private final double a0;
	private final double a1;

	/**
	 * Create a default Hamming window with default {@code a0}.
	 *
	 * @see #DEFAULT_A0
	 * @since 0.2.0
	 */
	public HammingWindow() {
		this(DEFAULT_A0);
	}

	/**
	 * @param a0 a0 constant of Hamming window
	 * @since 0.2.0
	 */
	public HammingWindow(double a0) {

		this.a0 = a0;

		a1 = 1 - a0;
	}

	@Override
	protected double getCoefficient(int n, int size) {
		return a0 - a1 * cos(TWO_PI * n / size);
	}
}

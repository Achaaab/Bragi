package com.github.achaaab.bragi.gui.component;

import static java.lang.Math.fma;
import static java.lang.Math.round;

/**
 * slider with floating point values and linear scale
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.6
 */
public class LinearSlider extends DecimalSlider {

	/**
	 * @param minimal minimal value of this slider
	 * @param maximal maximal value of this slider
	 * @param precision number of possible distinct values including {@code minimalValue} and {@code maximalValue}
	 * @since 0.2.0
	 */
	public LinearSlider(double minimal, double maximal, int precision) {
		super(minimal, maximal, precision);
	}

	@Override
	protected double getDecimalValue(int value) {
		return fma(value, amplitude / getMaximum(), minimal);
	}

	@Override
	protected int getValue(double decimalValue) {

		var value = getMaximum() * (decimalValue - minimal) / amplitude;
		return (int) round(value);
	}
}

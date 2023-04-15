package com.github.achaaab.bragi.gui.component;

import static java.lang.Math.fma;
import static java.lang.Math.round;

/**
 * range slider with floating point values and linear scale
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.3
 */
public class LinearRangeSlider extends DecimalRangeSlider {

	/**
	 * @param minimal minimal value of this slider
	 * @param maximal maximal value of this slider
	 * @param precision number of possible distinct values including {@code minimalValue} and {@code maximalValue}
	 * @since 0.2.0
	 */
	public LinearRangeSlider(double minimal, double maximal, int precision) {
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

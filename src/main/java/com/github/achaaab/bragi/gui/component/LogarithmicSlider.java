package com.github.achaaab.bragi.gui.component;

import static java.lang.Math.exp;
import static java.lang.Math.log;
import static java.lang.Math.pow;
import static java.lang.Math.round;

/**
 * slider with floating point values and logarithmic scale
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.6
 */
public class LogarithmicSlider extends DecimalSlider {

	private final double logarithmBase;
	private final double base;

	/**
	 * @param minimal minimal value of this slider
	 * @param maximal maximal value of this slider
	 * @param precision number of possible distinct values including {@code minimalValue} and {@code maximalValue}
	 * @since 0.2.0
	 */
	public LogarithmicSlider(double minimal, double maximal, int precision) {

		super(minimal, maximal, precision);

		logarithmBase = log(maximal / minimal) / precision;
		base = exp(logarithmBase);
	}

	@Override
	protected double getDecimalValue(int value) {
		return minimal * pow(base, value);
	}

	@Override
	protected int getValue(double decimalValue) {

		var value = log(decimalValue / minimal) / logarithmBase;
		return (int) round(value);
	}
}

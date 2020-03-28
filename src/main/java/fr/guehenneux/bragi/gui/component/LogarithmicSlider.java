package fr.guehenneux.bragi.gui.component;

import static java.lang.Math.exp;
import static java.lang.Math.log;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.pow;
import static java.lang.Math.round;

/**
 * Slider with decimal values and logarithmic scale.
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.6
 */
public class LogarithmicSlider extends DecimalSlider {

	private double logarithmBase;
	private double base;

	/**
	 * @param minimal   minimal value of this slider
	 * @param maximal   maximal value of this slider
	 * @param precision number of possible distinct values including {@code minimalValue} and {@code maximalValue}
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

		double value = log(decimalValue / minimal) / logarithmBase;
		return (int) round(value);
	}
}
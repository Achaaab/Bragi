package com.github.achaaab.bragi.gui.component;

import javax.swing.JSlider;

/**
 * @author Jonathan Guéhenneux
 * @since 0.0.6
 */
public abstract class DecimalSlider extends JSlider {

	protected double amplitude;
	protected double minimal;

	/**
	 * @param minimal   minimal value of this slider
	 * @param maximal   maximal value of this slider
	 * @param precision number of possible distinct values including {@code minimalValue} and {@code maximalValue}
	 */
	public DecimalSlider(double minimal, double maximal, int precision) {

		super(0, precision - 1);

		this.minimal = minimal;

		amplitude = maximal - minimal;
	}

	/**
	 * @return slider's current decimal value
	 */
	public double getDecimalValue() {
		return getDecimalValue(getValue());
	}

	/**
	 * @param value integer value of the slider
	 * @return corresponding decimal value
	 */
	protected abstract double getDecimalValue(int value);

	/**
	 * Sets the slider current value to {@code decimalValue}.
	 *
	 * @param decimalValue new decimal value
	 */
	public void setDecimalValue(double decimalValue) {
		setValue(getValue(decimalValue));
	}

	/**
	 * @param decimalValue decimal value
	 * @return corresponding integer value
	 */
	protected abstract int getValue(double decimalValue);
}
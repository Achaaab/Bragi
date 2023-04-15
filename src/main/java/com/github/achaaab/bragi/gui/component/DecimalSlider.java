package com.github.achaaab.bragi.gui.component;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSlider;
import java.text.DecimalFormat;
import java.util.Hashtable;

import static com.github.achaaab.bragi.gui.common.ViewScale.scale;

/**
 * slider with floating point values
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.6
 */
public abstract class DecimalSlider extends JSlider {

	private static final DecimalFormat LABEL_FORMAT = new DecimalFormat("0.##");

	protected double amplitude;
	protected double minimal;
	protected double maximal;

	/**
	 * @param minimal minimal value of this slider
	 * @param maximal maximal value of this slider
	 * @param precision number of possible distinct values including {@code minimalValue} and {@code maximalValue}
	 * @since 0.2.0
	 */
	public DecimalSlider(double minimal, double maximal, int precision) {

		super(0, precision);

		this.minimal = minimal;
		this.maximal = maximal;

		amplitude = maximal - minimal;
	}

	@Override
	public Hashtable<Integer, JComponent> createStandardLabels(int increment) {

		var labels = new Hashtable<Integer, JComponent>();

		var minimum = getMinimum();
		var maximum = getMaximum();

		for (var value = minimum; value <= maximum; value += increment) {
			labels.put(value, createStandardLabel(value));
		}

		return labels;
	}

	/**
	 * @param value value from which to create a label
	 * @return created label
	 * @since 0.2.0
	 */
	protected JComponent createStandardLabel(int value) {

		var decimalValue = getDecimalValue(value);
		var text = LABEL_FORMAT.format(decimalValue);
		var label = new JLabel(text);
		scale(label);

		return label;
	}

	/**
	 * @param value integer value of the slider
	 * @return corresponding decimal value
	 * @since 0.2.0
	 */
	protected abstract double getDecimalValue(int value);

	/**
	 * @param decimalValue decimal value
	 * @return corresponding integer value
	 * @since 0.2.0
	 */
	protected abstract int getValue(double decimalValue);

	/**
	 * @return slider's current decimal value
	 * @since 0.2.0
	 */
	public double getDecimalValue() {
		return getDecimalValue(getValue());
	}

	/**
	 * Sets the slider current value to {@code decimalValue}.
	 *
	 * @param decimalValue new decimal value
	 * @since 0.2.0
	 */
	public void setDecimalValue(double decimalValue) {
		setValue(getValue(decimalValue));
	}

	/**
	 * @param maximal maximal value of this slider
	 * @since 0.1.6
	 */
	public void setMaximal(double maximal) {

		this.maximal = maximal;

		amplitude = maximal - minimal;
	}
}

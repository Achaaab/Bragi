package com.github.achaaab.bragi.gui.component;

import com.github.achaaab.bragi.gui.common.ViewScale;

import javax.swing.JComponent;
import javax.swing.JLabel;
import java.text.DecimalFormat;
import java.util.Hashtable;

import static com.github.achaaab.bragi.gui.common.ViewScale.scale;

/**
 * range slider with floating point values
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.3
 */
public abstract class DecimalRangeSlider extends RangeSlider {

	private static final DecimalFormat LABEL_FORMAT = new DecimalFormat("0.##");

	protected final double amplitude;
	protected final double minimal;

	/**
	 * @param minimal   minimal value of this slider
	 * @param maximal   maximal value of this slider
	 * @param precision number of possible distinct values including {@code minimalValue} and {@code maximalValue}
	 */
	public DecimalRangeSlider(double minimal, double maximal, int precision) {

		super(0, precision);

		this.minimal = minimal;

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
	 */
	private JComponent createStandardLabel(int value) {

		var decimalValue = getDecimalValue(value);
		var text = LABEL_FORMAT.format(decimalValue);
		var label = new JLabel(text);
		scale(label);

		return label;
	}

	/**
	 * @param value integer value of the slider
	 * @return corresponding decimal value
	 */
	protected abstract double getDecimalValue(int value);

	/**
	 * @param decimalValue decimal value
	 * @return corresponding integer value
	 */
	protected abstract int getValue(double decimalValue);

	/**
	 * @return lower value of the selected range
	 */
	public double getDecimalLowerValue() {
		return getDecimalValue(getLowerValue());
	}

	/**
	 * @param decimalLowerValue new lower value
	 */
	public void setDecimalLowerValue(double decimalLowerValue) {
		setLowerValue(getValue(decimalLowerValue));
	}

	/**
	 * @return upper value of the selected range
	 */
	public double getDecimalUpperValue() {
		return getDecimalValue(getUpperValue());
	}

	/**
	 * @param decimalUpperValue new upper value
	 */
	public void setDecimalUpperValue(double decimalUpperValue) {
		setUpperValue(getValue(decimalUpperValue));
	}
}
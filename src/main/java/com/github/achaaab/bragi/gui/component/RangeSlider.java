package com.github.achaaab.bragi.gui.component;

import javax.swing.JSlider;

/**
 * An extension of JSlider to select a range of values using two thumb controls. The thumb controls are used
 * to select the lower and upper value of a range with predetermined minimum and maximum values.
 * <p>
 * Note that RangeSlider makes use of the default BoundedRangeModel, which supports an inner range
 * defined by a value and an extent. The upper value returned by RangeSlider is simply the lower value plus the extent.
 *
 * @author Ernest Yu
 * @author Jonathan Guéhenneux
 * @since 0.1.3
 */
public class RangeSlider extends JSlider {

	/**
	 * Constructs a RangeSlider with the specified default minimum and maximum values.
	 *
	 * @param minimum minimum value of the slider to create
	 * @param maximum maximum value of the slider to create
	 * @since 0.2.0
	 */
	public RangeSlider(int minimum, int maximum) {
		super(minimum, maximum);
	}

	@Override
	public void updateUI() {

		setUI(new RangeSliderUI(this));
		updateLabelUIs();
	}

	/**
	 * @return lower value
	 * @since 0.2.0
	 */
	public int getLowerValue() {
		return getValue();
	}

	/**
	 * @param lowerValue lower value to set
	 * @since 0.2.0
	 */
	public void setLowerValue(int lowerValue) {
		setValue(lowerValue);
	}

	@Override
	public void setValue(int lowerValue) {

		var oldLowerValue = getValue();
		var extent = getExtent() + oldLowerValue - lowerValue;

		getModel().setRangeProperties(lowerValue, extent, getMinimum(), getMaximum(), getValueIsAdjusting());
	}

	/**
	 * @return upper value
	 * @since 0.2.0
	 */
	public int getUpperValue() {
		return getValue() + getExtent();
	}

	/**
	 * @param upperValue upper value to set
	 * @since 0.2.0
	 */
	public void setUpperValue(int upperValue) {

		var lowerValue = getValue();
		setExtent(upperValue - lowerValue);
	}
}

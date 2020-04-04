package com.github.achaaab.bragi.gui.component;

import java.time.Duration;

import static java.time.Duration.ofSeconds;

/**
 * Time slider with millisecond resolution.
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.5
 */
public class TimeSlider extends LinearSlider {

	private static final Duration DEFAULT_DURATION = ofSeconds(25);

	/**
	 * Constructor with default duration.
	 *
	 * @see #DEFAULT_DURATION
	 */
	public TimeSlider() {

		super(0.0, DEFAULT_DURATION.toSeconds(), 1000);

		setDecimalValue(0.0);
	}
}
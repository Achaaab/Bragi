package com.github.achaaab.bragi.gui.component;

import javax.swing.border.TitledBorder;

import static java.lang.Math.pow;

/**
 * @author Jonathan Guéhenneux
 * @since 0.1.0
 */
public class FrequencySlider extends LogarithmicSlider {

	private static final int OCTAVE_COEFFICIENT = 12;

	/**
	 * @param minimalFrequency minimal frequency in hertz
	 * @param octaveCount      number of octave from the minimal frequency
	 */
	public FrequencySlider(double minimalFrequency, int octaveCount) {

		super(minimalFrequency, minimalFrequency * pow(2, octaveCount), OCTAVE_COEFFICIENT * octaveCount);

		setMajorTickSpacing(OCTAVE_COEFFICIENT);

		setPaintTrack(true);
		setPaintLabels(true);
		setPaintTicks(true);

		setBorder(new TitledBorder("Frequency in hertz"));
	}
}
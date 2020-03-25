package fr.guehenneux.bragi.module.view;

import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Hashtable;

import static java.lang.Math.log;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.pow;
import static java.lang.Math.round;

/**
 * @author Jonathan Guéhenneux
 */
public class FrequencySlider extends JSlider {

	private static final int OCTAVE_COEFFICIENT = 100;
	private static final double LOG_2 = log(2);
	private static final MathContext FREQUENCY_LABEL_MATH_CONTEXT = new MathContext(2);

	private double minimalFrequency;
	private int octaveCount;

	/**
	 * @param minimalFrequency minimal frequency in hertz
	 * @param octaveCount      number of octave from the minimal frequency
	 */
	public FrequencySlider(double minimalFrequency, int octaveCount) {

		this.minimalFrequency = minimalFrequency;
		this.octaveCount = octaveCount;

		setOrientation(HORIZONTAL);
		setMinimum(0);
		setMaximum(octaveCount * OCTAVE_COEFFICIENT);

		setPaintTrack(true);
		setPaintLabels(true);
		setPaintTicks(true);

		setMajorTickSpacing(OCTAVE_COEFFICIENT);

		computeLabels();

		setBorder(new TitledBorder("Frequency (Hz)"));
	}

	/**
	 * Compute labels.
	 */
	private void computeLabels() {

		var labels = new Hashtable<>();

		for (var octave = 0; octave <= octaveCount; octave++) {

			var sliderValue = octave * OCTAVE_COEFFICIENT;
			var frequency = getFrequency(octave * OCTAVE_COEFFICIENT);

			var frequencyText = BigDecimal.valueOf(frequency).
					round(FREQUENCY_LABEL_MATH_CONTEXT).
					toPlainString();

			labels.put(sliderValue, new JLabel(frequencyText));
		}

		setLabelTable(labels);
	}


	/**
	 * @param sliderValue slider
	 * @return corresponding frequency (Hz)
	 */
	private double getFrequency(double sliderValue) {
		return minimalFrequency * pow(2, sliderValue / OCTAVE_COEFFICIENT);
	}

	/**
	 * @param frequency frequency in hertz
	 * @return corresponding slider value
	 */
	private int getSliderValue(double frequency) {

		var theoreticalSliderValue = OCTAVE_COEFFICIENT * log(frequency / minimalFrequency) / LOG_2;
		var sliderValue = (int) round(theoreticalSliderValue);
		return min(octaveCount * OCTAVE_COEFFICIENT, max(0, sliderValue));
	}

	/**
	 * @return selected frequency (Hz)
	 */
	public double getFrequency() {
		return getFrequency(getValue());
	}

	/**
	 * @param frequency frequency to set in hertz
	 */
	public void setFrequency(double frequency) {
		setValue(getSliderValue(frequency));
	}
}
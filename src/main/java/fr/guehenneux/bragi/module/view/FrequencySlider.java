package fr.guehenneux.bragi.module.view;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.util.Dictionary;
import java.util.Hashtable;

/**
 * @author Jonathan Guéhenneux
 */
public class FrequencySlider extends JSlider {

  private static final int OCTAVE_COEFFICIENT = 100;

  private double minimalFrequency;
  private int octaveCount;

  /**
   * @param minimalFrequency
   * @param octaveCount
   */
  public FrequencySlider(double minimalFrequency, int octaveCount) {

    this.minimalFrequency = minimalFrequency;
    this.octaveCount = octaveCount;

    setOrientation(JSlider.HORIZONTAL);
    setMinimum(0);
    setMaximum(octaveCount * OCTAVE_COEFFICIENT);

    setPaintTrack(true);
    setPaintLabels(true);
    setPaintTicks(true);

    setMajorTickSpacing(OCTAVE_COEFFICIENT);
    setMinorTickSpacing(OCTAVE_COEFFICIENT / 5);

    computeLabels();

    setBorder(new TitledBorder("Frequency (Hz)"));
  }

  /**
   * Compute labels.
   */
  private void computeLabels() {

    Dictionary<Integer, JLabel> labels = new Hashtable<>();

    for (int octave = 0; octave <= octaveCount; octave++) {

      int sliderValue = octave * OCTAVE_COEFFICIENT;
      double frequency = getFrequency(octave * OCTAVE_COEFFICIENT);
      String text = Long.toString(Math.round(frequency));
      labels.put(sliderValue, new JLabel(text));
    }

    setLabelTable(labels);
  }

  /**
   * @param sliderValue
   * @return corresponding frequency (Hz)
   */
  private double getFrequency(double sliderValue) {
    return minimalFrequency * Math.pow(2, sliderValue / OCTAVE_COEFFICIENT);
  }

  /**
   * @return selected frequency (Hz)
   */
  public double getFrequency() {
    return getFrequency(getValue());
  }
}
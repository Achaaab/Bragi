package fr.guehenneux.bragi.module.view;

import fr.guehenneux.bragi.module.model.VCF;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import java.awt.GridLayout;

/**
 * @author Jonathan Guéhenneux
 */
public class VCFView extends JPanel {

  /**
   * @param model
   */
  public VCFView(VCF model) {

    FrequencySlider cutOffFrequencySlider = new FrequencySlider(25, 8);

    JSlider emphasisSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
    emphasisSlider.setMajorTickSpacing(25);
    emphasisSlider.setMinorTickSpacing(5);
    emphasisSlider.setPaintTicks(true);
    emphasisSlider.setPaintLabels(true);
    emphasisSlider.setBorder(new TitledBorder("Emphasis (%)"));

    setLayout(new GridLayout(2, 1));
    add(cutOffFrequencySlider);
    add(emphasisSlider);

    cutOffFrequencySlider.addChangeListener(changeEvent -> model.setCutOffFrequency((float)cutOffFrequencySlider.getFrequency()));
    emphasisSlider.addChangeListener(changeEvent -> model.setRezLevel(emphasisSlider.getValue()));

    JFrame frame = new JFrame(model.getName());
    frame.setSize(400, 300);
    frame.add(this);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
  }
}
package fr.guehenneux.bragi.module.view;

import fr.guehenneux.bragi.module.model.ADSR;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * @author Jonathan Guéhenneux
 */
public class ADSRView extends JPanel {

  /**
   * @param model
   */
  public ADSRView(ADSR model) {

    JSlider attackSlider = new JSlider(JSlider.HORIZONTAL, 0, 1000, 100);
    JSlider decaySlider = new JSlider(JSlider.HORIZONTAL, 0, 1000, 100);
    JSlider sustainSlider = new JSlider(JSlider.HORIZONTAL, -100, 100, 80);
    JSlider releaseSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);

    attackSlider.setBorder(new TitledBorder("Attack (V/s)"));
    decaySlider.setBorder(new TitledBorder("Decay (V/s)"));
    sustainSlider.setBorder(new TitledBorder("Sustain (V)"));
    releaseSlider.setBorder(new TitledBorder("Release (V/s)"));

    setLayout(new GridLayout(4, 1));
    add(attackSlider);
    add(decaySlider);
    add(sustainSlider);
    add(releaseSlider);

    attackSlider.addChangeListener(changeEvent -> model.setAttack(attackSlider.getValue() / 10.0));
    decaySlider.addChangeListener(changeEvent -> model.setDecay(decaySlider.getValue() / 10.0));
    sustainSlider.addChangeListener(changeEvent -> model.setSustain(sustainSlider.getValue() / 100.0));
    releaseSlider.addChangeListener(changeEvent -> model.setRelease(releaseSlider.getValue() / 10.0));

    JFrame frame = new JFrame(model.getName());
    frame.setSize(400, 300);
    frame.add(this);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
  }
}

package com.github.achaaab.bragi.gui.module;

import com.github.achaaab.bragi.gui.component.LinearSlider;
import com.github.achaaab.bragi.gui.component.LogarithmicSlider;
import com.github.achaaab.bragi.module.ADSR;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import java.awt.GridLayout;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

/**
 * ADSR Swing view
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.6
 */
public class ADSRView extends JPanel {

	/**
	 * @param model ADSR model
	 */
	public ADSRView(ADSR model) {

		var attackSlider = new LogarithmicSlider(1, 10000, 10);
		var decaySlider = new LogarithmicSlider(1, 10000, 10);
		var sustainSlider = new LinearSlider(-2.0, 0.0, 10);
		var releaseSlider = new LogarithmicSlider(1, 10000, 10);

		attackSlider.setBorder(new TitledBorder("Attack (V/s)"));
		decaySlider.setBorder(new TitledBorder("Decay (V/s)"));
		sustainSlider.setBorder(new TitledBorder("Sustain (V)"));
		releaseSlider.setBorder(new TitledBorder("Release (V/s)"));

		setLayout(new GridLayout(4, 1));
		add(attackSlider);
		add(decaySlider);
		add(sustainSlider);
		add(releaseSlider);

		attackSlider.setDecimalValue(model.getAttack());
		decaySlider.setDecimalValue(model.getDecay());
		sustainSlider.setDecimalValue(model.getSustain());
		releaseSlider.setDecimalValue(model.getRelease());

		attackSlider.addChangeListener(event -> model.setAttack(attackSlider.getDecimalValue()));
		decaySlider.addChangeListener(event -> model.setDecay(decaySlider.getDecimalValue()));
		sustainSlider.addChangeListener(event -> model.setSustain(sustainSlider.getDecimalValue()));
		releaseSlider.addChangeListener(event -> model.setRelease(releaseSlider.getDecimalValue()));

		var frame = new JFrame(model.getName());
		frame.setSize(400, 300);
		frame.setContentPane(this);
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
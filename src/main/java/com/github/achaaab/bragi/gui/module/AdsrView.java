package com.github.achaaab.bragi.gui.module;

import com.github.achaaab.bragi.core.module.producer.Adsr;
import com.github.achaaab.bragi.gui.component.LinearSlider;
import com.github.achaaab.bragi.gui.component.LogarithmicSlider;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.GridLayout;

import static com.github.achaaab.bragi.gui.common.ViewScale.scale;
import static javax.swing.BorderFactory.createTitledBorder;

/**
 * ADSR Swing view
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.6
 */
public class AdsrView extends JPanel {

	private static final Dimension SLIDERS_SIZE = scale(new Dimension(350, 60));

	/**
	 * @param model ADSR model
	 * @since 0.2.0
	 */
	public AdsrView(Adsr model) {

		var attackSlider = new LogarithmicSlider(0.1, 10000, 50);
		var decaySlider = new LogarithmicSlider(0.1, 10000, 50);
		var sustainSlider = new LinearSlider(-5.0, 0.0, 50);
		var releaseSlider = new LogarithmicSlider(0.1, 10000, 50);

		attackSlider.setBorder(createTitledBorder("Attack (V/s)"));
		decaySlider.setBorder(createTitledBorder("Decay (V/s)"));
		sustainSlider.setBorder(createTitledBorder("Sustain (V)"));
		releaseSlider.setBorder(createTitledBorder("Release (V/s)"));

		attackSlider.setMajorTickSpacing(10);
		decaySlider.setMajorTickSpacing(10);
		sustainSlider.setMajorTickSpacing(10);
		releaseSlider.setMajorTickSpacing(10);

		attackSlider.setPaintTicks(true);
		decaySlider.setPaintTicks(true);
		sustainSlider.setPaintTicks(true);
		releaseSlider.setPaintTicks(true);

		attackSlider.setPaintLabels(true);
		decaySlider.setPaintLabels(true);
		sustainSlider.setPaintLabels(true);
		releaseSlider.setPaintLabels(true);

		attackSlider.setPreferredSize(SLIDERS_SIZE);
		decaySlider.setPreferredSize(SLIDERS_SIZE);
		sustainSlider.setPreferredSize(SLIDERS_SIZE);
		releaseSlider.setPreferredSize(SLIDERS_SIZE);

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
	}
}

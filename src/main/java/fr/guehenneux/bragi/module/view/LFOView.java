package fr.guehenneux.bragi.module.view;

import fr.guehenneux.bragi.module.model.LFO;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.GridLayout;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

/**
 * @author Jonathan Guéhenneux
 */
public class LFOView extends JPanel {

	/**
	 * @param model
	 */
	public LFOView(LFO model) {

		// from 0.05Hz to 200Hz
		var frequencySlider = new FrequencySlider(0.05, 12);
		frequencySlider.addChangeListener(changeEvent -> model.setFrequency(frequencySlider.getFrequency()));

		setLayout(new GridLayout(1, 1));
		add(frequencySlider);

		var frame = new JFrame(model.getName());
		frame.setSize(400, 300);
		frame.setContentPane(this);
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
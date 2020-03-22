package fr.guehenneux.bragi.module.view;

import fr.guehenneux.bragi.module.model.LFO;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.GridLayout;

/**
 * @author Jonathan Guéhenneux
 */
public class LFOView extends JPanel {

	/**
	 * @param model
	 */
	public LFOView(LFO model) {

		// from 1Hz to 512Hz
		FrequencySlider frequencySlider = new FrequencySlider(1, 9);
		frequencySlider.addChangeListener(changeEvent -> model.setFrequency(frequencySlider.getFrequency()));

		setLayout(new GridLayout(1, 1));
		add(frequencySlider);

		JFrame frame = new JFrame(model.getName());
		frame.setSize(400, 300);
		frame.add(this);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
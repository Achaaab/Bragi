package fr.guehenneux.bragi.module.view;

import fr.guehenneux.bragi.module.model.LFO;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;

/**
 * @author Jonathan Guéhenneux
 */
public class LFOView extends JPanel {

	private LFO model;
	private JSlider frequencySlider;

	/**
	 * @param model
	 */
	public LFOView(LFO model) {

		this.model = model;

		// from 0.1Hz to 25.6Hz
		frequencySlider = new JSlider(JSlider.HORIZONTAL, 0, 800, 300);
		frequencySlider.addChangeListener(changeEvent -> model.setFrequency(0.1 * Math.pow(2, frequencySlider.getValue() / 100.0)));
		add(frequencySlider);

		JFrame frame = new JFrame(model.getName());
		frame.setSize(400, 300);
		frame.add(this);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
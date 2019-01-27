package fr.guehenneux.bragi.module;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;

/**
 * @author Jonathan Guéhenneux
 */
public class PresentationLFO extends JPanel {

	private LFO lfo;
	private JSlider frequencySlider;

	/**
	 * @param lfo
	 */
	public PresentationLFO(LFO lfo) {

		this.lfo = lfo;

		// from 0.1Hz to 25.6Hz
		frequencySlider = new JSlider(JSlider.HORIZONTAL, 0, 800, 300);
		frequencySlider.addChangeListener(changeEvent -> lfo.setFrequency(0.1 * Math.pow(2, frequencySlider.getValue() / 100.0)));
		add(frequencySlider);

		JFrame frame = new JFrame(lfo.getName());
		frame.setSize(400, 300);
		frame.add(this);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
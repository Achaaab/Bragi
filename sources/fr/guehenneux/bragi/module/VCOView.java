package fr.guehenneux.bragi.module;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;

/**
 * @author Jonathan Guéhenneux
 */
public class VCOView extends JPanel {

	private VCO model;
	private JSlider frequencySlider;

	/**
	 * @param model
	 */
	public VCOView(VCO model) {

		this.model = model;

		// from 25Hz to 12800Hz
		frequencySlider = new JSlider(JSlider.HORIZONTAL, 0, 900, 300);
		frequencySlider.addChangeListener(changeEvent -> model.setFrequency(25 * Math.pow(2, frequencySlider.getValue() / 100.0)));
		add(frequencySlider);

		JFrame frame = new JFrame(model.getName());
		frame.setSize(400, 300);
		frame.add(this);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
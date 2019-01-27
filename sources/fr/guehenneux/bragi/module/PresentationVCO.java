package fr.guehenneux.bragi.module;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;

/**
 * @author Jonathan Guéhenneux
 */
public class PresentationVCO extends JPanel {

	private VCO vco;
	private JSlider frequencySlider;

	/**
	 * @param vco
	 */
	public PresentationVCO(VCO vco) {

		this.vco = vco;

		// from 25Hz to 12800Hz
		frequencySlider = new JSlider(JSlider.HORIZONTAL, 0, 900, 300);
		frequencySlider.addChangeListener(changeEvent -> vco.setFrequency(25 * Math.pow(2, frequencySlider.getValue() / 100.0)));
		add(frequencySlider);

		JFrame frame = new JFrame(vco.getName());
		frame.setSize(400, 300);
		frame.add(this);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
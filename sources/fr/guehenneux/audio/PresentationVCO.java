package fr.guehenneux.audio;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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

		// from 50Hz to 12800Hz
		frequencySlider = new JSlider(JSlider.HORIZONTAL, 0, 800, 300);
		frequencySlider.addChangeListener(changeEvent -> vco.setFrequency(50 * Math.pow(2, frequencySlider.getValue() / 100.0)));
		add(frequencySlider);

		JFrame frame = new JFrame("VCO");
		frame.setSize(400, 300);
		frame.add(this);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
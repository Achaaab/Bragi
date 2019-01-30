package fr.guehenneux.bragi.module.view;

import fr.guehenneux.bragi.module.model.VCO;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import java.awt.*;

/**
 * @author Jonathan Guéhenneux
 */
public class VCOView extends JPanel {

	/**
	 * @param model
	 */
	public VCOView(VCO model) {

		// from 25Hz to 12800Hz
		FrequencySlider frequencySlider = new FrequencySlider(25, 9);
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
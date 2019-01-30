package fr.guehenneux.bragi.module.view;

import fr.guehenneux.bragi.module.model.VCF;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;

/**
 * @author Jonathan Guéhenneux
 */
public class VCFView extends JPanel {

	private VCF model;

	private JSlider cutOffFrequencySlider;
	private JLabel cutOffFrequencyLabel;

	/**
	 * @param model
	 */
	public VCFView(VCF model) {

		this.model = model;

		cutOffFrequencySlider = new JSlider(JSlider.HORIZONTAL, 0, 900, 300);
		cutOffFrequencySlider.setPaintLabels(false);
		cutOffFrequencySlider.setPaintTicks(true);

		cutOffFrequencyLabel = new JLabel();

		add(cutOffFrequencySlider);
		add(cutOffFrequencyLabel);

		cutOffFrequencySlider.addChangeListener(this::updateCutOffFrequency);

		JFrame container = new JFrame(model.getName());
		container.setSize(400, 300);
		container.add(this);
		container.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		container.setVisible(true);
	}

	/**
	 * @param changeEvent
	 */
	private void updateCutOffFrequency(ChangeEvent changeEvent) {

		// from 25Hz to 12800Hz
		float cutOffFrequency = (float) (25 * Math.pow(2, cutOffFrequencySlider.getValue() / 100.0));
		cutOffFrequencyLabel.setText((int) cutOffFrequency + "Hz");
		model.setCutOffFrequency(cutOffFrequency);
	}
}
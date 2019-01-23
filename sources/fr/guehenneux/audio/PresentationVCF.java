package fr.guehenneux.audio;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * 
 * @author Jonathan
 * 
 */
public class PresentationVCF extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6842872974689035898L;

	private VCF vcf;

	private JSlider cutOffFrequencySlider;

	private JLabel cutOffFrequencyLabel;

	/**
	 * 
	 * @param vcf
	 */
	public PresentationVCF(VCF vcf) {

		this.vcf = vcf;

		cutOffFrequencySlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
		cutOffFrequencySlider.setPaintLabels(false);
		cutOffFrequencySlider.setPaintTicks(true);

		cutOffFrequencyLabel = new JLabel();

		add(cutOffFrequencySlider);
		add(cutOffFrequencyLabel);

		cutOffFrequencySlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				updateCutOffFrequency();
			}

		});

		JFrame container = new JFrame(vcf.getName());
		container.setSize(400, 300);
		container.add(this);
		container.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		container.setVisible(true);

	}

	private void updateCutOffFrequency() {

		int sliderValue = cutOffFrequencySlider.getValue();
		float cutOffFrequency = (float) (Math.pow(1.1, sliderValue));
		cutOffFrequencyLabel.setText((int) cutOffFrequency + "Hz");
		vcf.setCutOffFrequency(cutOffFrequency);

	}

}

package fr.guehenneux.audio;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * 
 * @author GUEHENNEUX
 *
 */
public class PresentationVCO extends JPanel {

	/**
     * 
     */
	private static final long serialVersionUID = 5223257961962194357L;

	private VCO vco;
	private JSlider frequencySlider;

	/**
     * 
     *
     */
	public PresentationVCO(VCO vco) {

		this.vco = vco;
		frequencySlider = new JSlider(JSlider.HORIZONTAL, 50, 22050, 440);
		frequencySlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				getVco().setFrequency(frequencySlider.getValue());
			}
		});

		add(frequencySlider);

		JFrame container = new JFrame("VCO");
		container.setSize(400, 300);
		container.add(this);
		container.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		container.setVisible(true);

	}

	/**
	 * @return vco
	 */
	public VCO getVco() {
		return vco;
	}

}

package fr.guehenneux.bragi.module.view;

import fr.guehenneux.bragi.module.model.VCO;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import java.awt.GridLayout;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

/**
 * VCO view
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.4
 */
public class VCOView extends JPanel {

	/**
	 * @param model VCO model
	 */
	public VCOView(VCO model) {

		var waveformComboBox = new WaveformComboBox();
		waveformComboBox.addActionListener(actionEvent -> model.setWaveform(waveformComboBox.getSelectedWaveform()));

		var octaveSlider = new JSlider(-4, 4);
		octaveSlider.setBorder(new TitledBorder("Octave"));
		octaveSlider.setMajorTickSpacing(1);
		octaveSlider.setPaintTicks(true);
		octaveSlider.setPaintLabels(true);
		octaveSlider.addChangeListener(changeEvent -> model.setOctave(octaveSlider.getValue()));

		setLayout(new BorderLayout());
		add(waveformComboBox, NORTH);
		add(octaveSlider, CENTER);

		var frame = new JFrame(model.getName());
		frame.setContentPane(this);
		frame.setSize(400, 300);
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
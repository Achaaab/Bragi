package com.github.achaaab.bragi.gui.module;

import com.github.achaaab.bragi.gui.component.LinearSlider;
import com.github.achaaab.bragi.module.DCG;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

/**
 * DCG view
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.6
 */
public class DCGView extends JPanel {

	/**
	 * @param model DCG model
	 */
	public DCGView(DCG model) {

		var minimalVoltage = model.getMinimalVoltage();
		var maximalVoltage = model.getMaximalVoltage();
		var voltage = model.getVoltage();

		var voltageSlider = new LinearSlider(minimalVoltage, maximalVoltage, 1000);
		voltageSlider.setMajorTickSpacing(100);
		voltageSlider.setPaintTicks(true);
		voltageSlider.setPaintLabels(true);
		voltageSlider.setBorder(BorderFactory.createTitledBorder("Voltage (V)"));

		voltageSlider.setDecimalValue(voltage);

		voltageSlider.addChangeListener(event -> model.setVoltage((float) voltageSlider.getDecimalValue()));

		setLayout(new BorderLayout());
		add(voltageSlider, BorderLayout.CENTER);

		var frame = new JFrame(model.getName());
		frame.setContentPane(this);
		frame.setSize(400, 200);
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
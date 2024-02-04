package com.github.achaaab.bragi.gui.module;

import com.github.achaaab.bragi.core.module.producer.Dcg;
import com.github.achaaab.bragi.gui.component.LinearSlider;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;

import static com.github.achaaab.bragi.gui.common.ViewScale.scale;

/**
 * DCG view
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.6
 */
public class DcgView extends JPanel {

	private static final Dimension SLIDERS_SIZE = scale(new Dimension(350, 60));

	/**
	 * @param model DCG model
	 * @since 0.2.0
	 */
	public DcgView(Dcg model) {

		var minimalVoltage = model.getMinimalVoltage();
		var maximalVoltage = model.getMaximalVoltage();
		var voltage = model.getVoltage();

		var voltageSlider = new LinearSlider(minimalVoltage, maximalVoltage, 1000);
		voltageSlider.setMajorTickSpacing(100);
		voltageSlider.setPaintTicks(true);
		voltageSlider.setPaintLabels(true);
		voltageSlider.setBorder(BorderFactory.createTitledBorder("Voltage (V)"));

		voltageSlider.setPreferredSize(SLIDERS_SIZE);

		voltageSlider.setDecimalValue(voltage);

		voltageSlider.addChangeListener(event -> model.setVoltage((float) voltageSlider.getDecimalValue()));

		setLayout(new BorderLayout());
		add(voltageSlider, BorderLayout.CENTER);
	}
}

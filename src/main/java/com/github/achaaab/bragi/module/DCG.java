package com.github.achaaab.bragi.module;

import com.github.achaaab.bragi.common.Settings;
import com.github.achaaab.bragi.common.connection.Output;
import com.github.achaaab.bragi.gui.module.DCGView;

import static javax.swing.SwingUtilities.invokeLater;

/**
 * Direct Current Generator
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.6
 */
public class DCG extends Module {

	public static final String DEFAULT_NAME = "dcg";

	private final Output output;

	private final float minimalVoltage;
	private final float maximalVoltage;

	private float voltage;

	/**
	 * Creates a DCG with default name.
	 *
	 * @see #DEFAULT_NAME
	 */
	public DCG() {
		this(DEFAULT_NAME);
	}

	/**
	 * Creates a DCG with specified name and initial voltage between minimal voltage and maximal voltage.
	 *
	 * @param name name of the DCG to create
	 */
	public DCG(String name) {

		super(name);

		output = addPrimaryOutput("_output");

		minimalVoltage = Settings.INSTANCE.minimalVoltage();
		maximalVoltage = Settings.INSTANCE.maximalVoltage();

		voltage = (minimalVoltage + maximalVoltage) / 2;

		invokeLater(() -> new DCGView(this));

		start();
	}

	@Override
	protected int compute() throws InterruptedException {

		var sampleCount = Settings.INSTANCE.chunkSize();

		var samples = new float[sampleCount];

		for (var sampleIndex = 0; sampleIndex < sampleCount; sampleIndex++) {
			samples[sampleIndex] = voltage;
		}

		output.write(samples);
		return sampleCount;
	}

	/**
	 * @return minimal voltage of this DCG in volts (V)
	 */
	public float getMinimalVoltage() {
		return minimalVoltage;
	}

	/**
	 * @return maximal voltage of this DCG in volts (V)
	 */
	public float getMaximalVoltage() {
		return maximalVoltage;
	}

	/**
	 * @return voltage of this DCG in volts (V)
	 */
	public float getVoltage() {
		return voltage;
	}

	/**
	 * @param voltage new voltage to set in volts (V)
	 */
	public void setVoltage(float voltage) {
		this.voltage = voltage;
	}
}
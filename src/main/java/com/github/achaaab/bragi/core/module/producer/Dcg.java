package com.github.achaaab.bragi.core.module.producer;

import com.github.achaaab.bragi.common.Settings;
import com.github.achaaab.bragi.core.connection.Output;
import com.github.achaaab.bragi.core.module.Module;
import com.github.achaaab.bragi.core.module.ModuleCreationException;
import com.github.achaaab.bragi.gui.module.DcgView;

import java.lang.reflect.InvocationTargetException;

import static java.util.Arrays.fill;
import static javax.swing.SwingUtilities.invokeAndWait;

/**
 * Direct Current Generator
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.6
 */
public class Dcg extends Module {

	public static final String DEFAULT_NAME = "dcg";

	private final Output output;

	private final float minimalVoltage;
	private final float maximalVoltage;

	private float voltage;

	/**
	 * Creates a DCG with default name.
	 *
	 * @see #DEFAULT_NAME
	 * @since 0.2.0
	 */
	public Dcg() {
		this(DEFAULT_NAME);
	}

	/**
	 * Creates a DCG with specified name and initial voltage between minimal voltage and maximal voltage.
	 *
	 * @param name name of the DCG to create
	 * @since 0.2.0
	 */
	public Dcg(String name) {

		super(name);

		output = addPrimaryOutput("_output");

		minimalVoltage = Settings.INSTANCE.minimalVoltage();
		maximalVoltage = Settings.INSTANCE.maximalVoltage();

		voltage = (minimalVoltage + maximalVoltage) / 2;

		try {
			invokeAndWait(() -> view = new DcgView(this));
		} catch (InterruptedException | InvocationTargetException cause) {
			throw new ModuleCreationException(cause);
		}
	}

	@Override
	protected int compute() throws InterruptedException {

		var sampleCount = Settings.INSTANCE.chunkSize();

		var samples = new float[sampleCount];
		fill(samples, voltage);

		output.write(samples);
		return sampleCount;
	}

	/**
	 * @return minimal voltage of this DCG in volts (V)
	 * @since 0.2.0
	 */
	public float getMinimalVoltage() {
		return minimalVoltage;
	}

	/**
	 * @return maximal voltage of this DCG in volts (V)
	 * @since 0.2.0
	 */
	public float getMaximalVoltage() {
		return maximalVoltage;
	}

	/**
	 * @return voltage of this DCG in volts (V)
	 * @since 0.2.0
	 */
	public float getVoltage() {
		return voltage;
	}

	/**
	 * @param voltage new voltage to set in volts (V)
	 * @since 0.2.0
	 */
	public void setVoltage(float voltage) {
		this.voltage = voltage;
	}
}

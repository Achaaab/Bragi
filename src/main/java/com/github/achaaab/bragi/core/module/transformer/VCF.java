package com.github.achaaab.bragi.core.module.transformer;

import com.github.achaaab.bragi.common.Normalizer;
import com.github.achaaab.bragi.common.Settings;
import com.github.achaaab.bragi.core.connection.Input;
import com.github.achaaab.bragi.core.connection.Output;
import com.github.achaaab.bragi.core.module.Module;
import com.github.achaaab.bragi.core.module.ModuleCreationException;
import com.github.achaaab.bragi.gui.module.VCFView;

import java.lang.reflect.InvocationTargetException;

import static javax.swing.SwingUtilities.invokeAndWait;

/**
 * Voltage-Controlled Filter
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.9
 */
public abstract class VCF extends Module {

	protected static final double VOLTS_PER_OCTAVE = 5.0;

	protected static final Normalizer NORMALIZER = new Normalizer(
			Settings.INSTANCE.minimalVoltage(), Settings.INSTANCE.maximalVoltage(),
			-1.0f, 1.0f
	);

	protected final Input modulation;
	protected final Input input;
	protected final Output output;

	protected float emphasis;
	protected float cutoffFrequency;
	protected double actualCutoffFrequency;

	protected float[] inputSamples;
	protected float[] modulationSamples;
	protected float modulationSample;
	protected float[] outputSamples;

	protected double y1;
	protected double y2;
	protected double y3;
	protected double y4;
	protected double oldX;
	protected double oldY1;
	protected double oldY2;
	protected double oldY3;

	/**
	 * @param name name of the VCF to create
	 * @since 0.0.9
	 */
	public VCF(String name) {

		super(name);

		input = addPrimaryInput(name + "_input");
		modulation = addSecondaryInput(name + "_modulation");
		output = addPrimaryOutput(name + "_output");

		emphasis = 0.5f;
		cutoffFrequency = 440.0f;

		y1 = 0.0;
		y2 = 0.0;
		y3 = 0.0;
		y4 = 0.0;
		oldX = 0.0;
		oldY1 = 0.0;
		oldY2 = 0.0;
		oldY3 = 0.0;

		try {
			invokeAndWait(() -> view = new VCFView(this));
		} catch (InterruptedException | InvocationTargetException cause) {
			throw new ModuleCreationException(cause);
		}
	}

	@Override
	public int compute() throws InterruptedException {

		inputSamples = input.read();
		modulationSamples = modulation.read();

		filterSamples();

		output.write(outputSamples);

		return inputSamples.length;
	}

	/**
	 * filter samples
	 */
	protected abstract void filterSamples();

	/**
	 * @return modulation input
	 */
	public Input getModulation() {
		return modulation;
	}

	/**
	 * @return how much emphasis around cutoff frequency in {@code [0.0f, 1.0f]}
	 */
	public float getEmphasis() {
		return emphasis;
	}

	/**
	 * @param emphasis how much emphasis around cutoff frequency in {@code [0.0f, 1.0f]}
	 */
	public void setEmphasis(float emphasis) {
		this.emphasis = emphasis;
	}

	/**
	 * @return base frequency (without modulation) from which this VCF starts to filter
	 */
	public float getCutoffFrequency() {
		return cutoffFrequency;
	}

	/**
	 * @param cutoffFrequency base frequency (without modulation) from which this VCF starts to filter
	 */
	public void setCutoffFrequency(float cutoffFrequency) {
		this.cutoffFrequency = cutoffFrequency;
	}
}
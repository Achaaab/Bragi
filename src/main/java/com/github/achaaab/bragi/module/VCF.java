package com.github.achaaab.bragi.module;

import com.github.achaaab.bragi.common.connection.Input;
import com.github.achaaab.bragi.common.connection.Output;
import com.github.achaaab.bragi.gui.module.VCFView;

import static javax.swing.SwingUtilities.invokeLater;

/**
 * Voltage-Controlled Filter
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.9
 */
public abstract class VCF extends Module {

	protected final Input modulation;
	protected final Input input;
	protected final Output output;

	protected float emphasis;
	protected float cutOffFrequency;
	protected double actualCutOffFrequency;

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
		cutOffFrequency = 440.0f;

		y1 = 0.0;
		y2 = 0.0;
		y3 = 0.0;
		y4 = 0.0;
		oldX = 0.0;
		oldY1 = 0.0;
		oldY2 = 0.0;
		oldY3 = 0.0;

		invokeLater(() -> new VCFView(this));

		start();
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
	 * @return rezLevel
	 */
	public float getEmphasis() {
		return emphasis;
	}

	/**
	 * @param emphasis amount of emphasis between 0 and 1
	 */
	public void setEmphasis(float emphasis) {
		this.emphasis = emphasis;
	}

	/**
	 * @return cutOffFrequency
	 */
	public float getCutOffFrequency() {
		return cutOffFrequency;
	}

	/**
	 * @param cutOffFrequency cutOffFrequency to set
	 */
	public void setCutOffFrequency(float cutOffFrequency) {
		this.cutOffFrequency = cutOffFrequency;
	}
}
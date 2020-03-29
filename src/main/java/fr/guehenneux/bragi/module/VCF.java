package fr.guehenneux.bragi.module;

import fr.guehenneux.bragi.common.connection.Input;
import fr.guehenneux.bragi.common.connection.Output;
import fr.guehenneux.bragi.gui.module.VCFView;

/**
 * Voltage-Controlled Filter
 *
 * @author Jonathan Guéhenneux
 */
public abstract class VCF extends Module {

	protected Input modulation;
	protected Input input;
	protected Output output;

	protected float emphasis;
	protected float cutOffFrequency;
	protected double actualCutOffFrequency;

	protected float[] inputSamples;
	protected float[] modulationSamples;
	protected float modulationSample;
	protected float[] outputSamples;

	protected double f0, f1;

	protected double y1, y2, y3, y4, oldx, oldy1, oldy2, oldy3;

	/**
	 * @param name
	 */
	public VCF(String name) {

		super(name);

		input = addPrimaryInput(name + "_input");
		modulation = addSecondaryInput(name + "_modulation");
		output = addPrimaryOutput(name + "_output");

		emphasis = 0.5f;
		cutOffFrequency = 440.0f;

		f0 = f1 = y1 = y2 = y3 = y4 = oldx = oldy1 = oldy2 = oldy3 = 0.0;

		new VCFView(this);

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
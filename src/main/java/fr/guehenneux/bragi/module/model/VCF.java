package fr.guehenneux.bragi.module.model;

import fr.guehenneux.bragi.connection.Input;
import fr.guehenneux.bragi.connection.Output;
import fr.guehenneux.bragi.module.view.VCFView;

/**
 * Voltage-Controlled Filter
 *
 * @author Jonathan Guéhenneux
 */
public abstract class VCF extends Module {

	protected Input modulation;
	protected Input input;
	protected Output output;

	protected float rezLevel;
	protected float cutOffFrequency;
	protected float actualCutOffFrequency;

	protected float[] inputSamples;
	protected float[] modulationSamples;
	protected float modulationSample;
	protected float[] outputSamples;

	protected float f0, f1;

	protected float y1, y2, y3, y4, oldx, oldy1, oldy2, oldy3;

	/**
	 * @param name
	 */
	public VCF(String name) {

		super(name);

		input = addInput(name + "_input");
		modulation = addInput(name + "_modulation");
		output = addOutput(name + "_output");

		rezLevel = 0.5f;
		cutOffFrequency = 440.0f;

		f0 = f1 = y1 = y2 = y3 = y4 = oldx = oldy1 = oldy2 = oldy3 = 0.0f;

		new VCFView(this);

		start();
	}

	@Override
	public int compute() throws InterruptedException {

		inputSamples = input.read();
		modulationSamples = modulation.tryRead();

		filterSamples();

		output.write(outputSamples);

		return inputSamples.length;
	}

	/**
	 *
	 * @return
	 */
	protected abstract void filterSamples();

	/**
	 * @return the modulation
	 */
	public Input getModulation() {
		return modulation;
	}

	/**
	 * @return the rezLevel
	 */
	public float getRezLevel() {
		return rezLevel;
	}

	/**
	 * @param rezLevel
	 *            the rezLevel to set
	 */
	public void setRezLevel(float rezLevel) {
		this.rezLevel = rezLevel;
	}

	/**
	 * @return the cutOffFrequency
	 */
	public float getCutOffFrequency() {
		return cutOffFrequency;
	}

	/**
	 * @param cutOffFrequency
	 *            the cutOffFrequency to set
	 */
	public void setCutOffFrequency(float cutOffFrequency) {
		this.cutOffFrequency = cutOffFrequency;
	}
}
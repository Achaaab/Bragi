package fr.guehenneux.bragi.module;

/**
 * @author Jonathan Guéhenneux
 */
public abstract class VCF extends Module {

	protected Input modulationPort;
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

	protected boolean modulation;

	protected PresentationVCF presentation;

	/**
	 * @param name
	 */
	public VCF(String name) {

		super(name);

		input = addInput(name + "_input");
		modulationPort = addInput(name + "_modulation");
		output = addOutput(name + "_output");

		rezLevel = 0.5f;
		cutOffFrequency = 440.0f;

		f0 = f1 = y1 = y2 = y3 = y4 = oldx = oldy1 = oldy2 = oldy3 = 0.0f;

		presentation = new PresentationVCF(this);
	}

	@Override
	public void compute() throws InterruptedException {

		inputSamples = input.read();

		if (modulationPort.isReady()) {

			modulationSamples = modulationPort.read();
			modulation = true;

		} else {

			modulation = false;
		}

		filterSamples();
		output.write(outputSamples);
	}

	/**
	 *
	 * @return
	 */
	protected abstract void filterSamples();

	/**
	 * @return the modulationPort
	 */
	public Input getModulationPort() {
		return modulationPort;
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
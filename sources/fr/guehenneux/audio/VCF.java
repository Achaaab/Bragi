package fr.guehenneux.audio;

/**
 * 
 * @author Jonathan
 * 
 */
public abstract class VCF extends Module {

	protected InputPort modulationPort;
	protected InputPort inputPort;
	protected OutputPort outputPort;

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
	 * 
	 * @param name
	 */
	public VCF(String name) {

		super(name);

		modulationPort = new InputPort();
		inputPort = new InputPort();
		outputPort = new OutputPort();

		rezLevel = 0.5f;
		cutOffFrequency = 440.0f;

		f0 = f1 = y1 = y2 = y3 = y4 = oldx = oldy1 = oldy2 = oldy3 = 0.0f;

		presentation = new PresentationVCF(this);
	}

	@Override
	public void compute() {

		if (outputPort.isConnected() && inputPort.isConnected()) {

			inputSamples = inputPort.read();

			if (modulationPort.isConnected() && modulationPort.isReady()) {

				modulationSamples = modulationPort.read();
				modulation = true;

			} else {

				modulation = false;
			}

			filterSamples();
			outputPort.write(outputSamples);
		}
	}

	/**
	 * 
	 * @return
	 */
	protected abstract void filterSamples();

	/**
	 * @return the modulationPort
	 */
	public InputPort getModulationPort() {
		return modulationPort;
	}

	/**
	 * @return the inputPort
	 */
	public InputPort getInputPort() {
		return inputPort;
	}

	/**
	 * @return the outputPort
	 */
	public OutputPort getOutputPort() {
		return outputPort;
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
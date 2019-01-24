package fr.guehenneux.audio;

/**
 * @author Jonathan Guéhenneux
 */
public class Oscilloscope extends Module {

	private InputPort inputPort;
	private PresentationOscilloscope presentation;

	/**
	 * @param name
	 */
	public Oscilloscope(String name) {

		super(name);

		inputPort = new InputPort();
		presentation = new PresentationOscilloscope(this);
	}

	@Override
	public void compute() throws InterruptedException {

		float[] samples = inputPort.read();
		presentation.display(samples);
	}

	/**
	 * @return inputPort
	 */
	public InputPort getInputPort() {
		return inputPort;
	}
}
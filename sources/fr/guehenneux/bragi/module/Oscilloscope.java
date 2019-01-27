package fr.guehenneux.bragi.module;

/**
 * @author Jonathan Guéhenneux
 */
public class Oscilloscope extends Module {

	private Input input;
	private PresentationOscilloscope presentation;

	/**
	 * @param name
	 */
	public Oscilloscope(String name) {

		super(name);

		input = new Input();
		presentation = new PresentationOscilloscope(this);
	}

	@Override
	public void compute() throws InterruptedException {

		float[] samples = input.read();
		presentation.afficher(samples);
	}

	/**
	 * @return input
	 */
	public Input getInput() {
		return input;
	}
}
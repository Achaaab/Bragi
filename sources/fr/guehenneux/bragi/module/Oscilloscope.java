package fr.guehenneux.bragi.module;

import fr.guehenneux.bragi.Settings;

/**
 * @author Jonathan Guéhenneux
 */
public class Oscilloscope extends RealTimeModule {

	private Input input;
	private PresentationOscilloscope presentation;

	/**
	 * @param name
	 */
	public Oscilloscope(String name) {

		super(name);

		input = addInput(name + "_input");
		presentation = new PresentationOscilloscope(this);

		start();
	}

	@Override
	protected double realTimeCompute() throws InterruptedException {

		float[] samples = input.read();
		presentation.display(samples);

		double sampleCount = samples.length;
		int samplingRate = Settings.INSTANCE.getFrameRate();
		return sampleCount / samplingRate;
	}
}
package fr.guehenneux.bragi.module.model;

import fr.guehenneux.bragi.Settings;
import fr.guehenneux.bragi.connection.Input;
import fr.guehenneux.bragi.module.view.OscilloscopeView;

/**
 * @author Jonathan Guéhenneux
 */
public class Oscilloscope extends RealTimeModule {

	private Input input;
	private OscilloscopeView presentation;

	/**
	 * @param name
	 */
	public Oscilloscope(String name) {

		super(name);

		input = addInput(name + "_input");
		presentation = new OscilloscopeView(this);

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
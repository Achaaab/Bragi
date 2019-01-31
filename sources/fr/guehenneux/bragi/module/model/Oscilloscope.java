package fr.guehenneux.bragi.module.model;

import fr.guehenneux.bragi.Settings;
import fr.guehenneux.bragi.ShiftingFloatArray;
import fr.guehenneux.bragi.connection.Input;
import fr.guehenneux.bragi.module.view.OscilloscopeView;

/**
 * @author Jonathan Guéhenneux
 */
public class Oscilloscope extends Module {

	private Input input;

	private ShiftingFloatArray buffer;

	private OscilloscopeView presentation;

	/**
	 * @param name oscilloscope name
	 */
	public Oscilloscope(String name) {

		super(name);

		input = addInput(name + "_input");

		int oscilloscopeSampleCount = Settings.INSTANCE.getFrameRate() / 60;
		buffer = new ShiftingFloatArray(oscilloscopeSampleCount);

		presentation = new OscilloscopeView(this);
		start();
	}

	@Override
	protected void compute() throws InterruptedException {

		float[] samples = input.read();

		synchronized (buffer) {
			buffer.write(samples);
		}
	}

	/**
	 * @return oscilloscope buffer
	 */
	public ShiftingFloatArray getBuffer() {
		return buffer;
	}
}
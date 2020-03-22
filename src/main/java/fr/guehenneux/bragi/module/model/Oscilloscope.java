package fr.guehenneux.bragi.module.model;

import fr.guehenneux.bragi.Settings;
import fr.guehenneux.bragi.ShiftingFloatArray;
import fr.guehenneux.bragi.connection.Input;
import fr.guehenneux.bragi.module.view.OscilloscopeView;

/**
 * @author Jonathan Guéhenneux
 */
public class Oscilloscope extends RealTimeModule {

	private Input input;

	private final ShiftingFloatArray buffer;

	/**
	 * @param name oscilloscope name
	 */
	public Oscilloscope(String name) {

		super(name);

		input = addInput(name + "_input");

		int oscilloscopeSampleCount = Settings.INSTANCE.getFrameRate() / 60;
		buffer = new ShiftingFloatArray(oscilloscopeSampleCount);

		new OscilloscopeView(this);
		start();
	}

	@Override
	protected int realTimeCompute() throws InterruptedException {

		float[] samples = input.read();

		synchronized (buffer) {
			buffer.write(samples);
		}

		return samples.length;
	}

	/**
	 * @return oscilloscope buffer
	 */
	public ShiftingFloatArray getBuffer() {
		return buffer;
	}
}
package fr.guehenneux.bragi.module;

import fr.guehenneux.bragi.common.Settings;
import fr.guehenneux.bragi.common.ShiftingFloatArray;
import fr.guehenneux.bragi.common.connection.Input;
import fr.guehenneux.bragi.gui.module.OscilloscopeView;

/**
 * @author Jonathan Guéhenneux
 */
public class Oscilloscope extends Module {

	private Input input;
	private final ShiftingFloatArray buffer;

	/**
	 * @param name oscilloscope name
	 */
	public Oscilloscope(String name) {

		super(name);

		input = addPrimaryInput(name + "_input");

		var oscilloscopeSampleCount = Settings.INSTANCE.getFrameRate() / 60;
		buffer = new ShiftingFloatArray(oscilloscopeSampleCount);

		new OscilloscopeView(this);

		start();
	}

	@Override
	protected int compute() throws InterruptedException {

		var samples = input.read();

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
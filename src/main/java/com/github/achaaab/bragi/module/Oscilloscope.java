package com.github.achaaab.bragi.module;

import com.github.achaaab.bragi.common.Settings;
import com.github.achaaab.bragi.common.CircularFloatArray;
import com.github.achaaab.bragi.common.connection.Input;
import com.github.achaaab.bragi.gui.module.OscilloscopeView;

/**
 * @author Jonathan Guéhenneux
 */
public class Oscilloscope extends Module {

	private Input input;
	private final CircularFloatArray buffer;

	/**
	 * @param name oscilloscope name
	 */
	public Oscilloscope(String name) {

		super(name);

		input = addPrimaryInput(name + "_input");

		var oscilloscopeSampleCount = Settings.INSTANCE.getFrameRate();
		buffer = new CircularFloatArray(oscilloscopeSampleCount);

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
	 * @param readArray
	 */
	public void read(float[] readArray) {

		synchronized (buffer) {
			buffer.readLast(readArray);
		}
	}
}
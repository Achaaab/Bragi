package com.github.achaaab.bragi.core.module.consumer;

import com.github.achaaab.bragi.common.CircularFloatArray;
import com.github.achaaab.bragi.common.Settings;
import com.github.achaaab.bragi.core.connection.Input;
import com.github.achaaab.bragi.gui.module.OscilloscopeView;
import com.github.achaaab.bragi.core.module.Module;
import org.slf4j.Logger;

import static javax.swing.SwingUtilities.invokeLater;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * oscilloscope
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.9
 */
public class Oscilloscope extends Module {

	private static final Logger LOGGER = getLogger(Oscilloscope.class);

	public static final String DEFAULT_NAME = "oscilloscope";

	private final Input input;
	private final CircularFloatArray buffer;

	/**
	 * Creates an oscilloscope with default name.
	 *
	 * @see #DEFAULT_NAME
	 * @since 0.0.9
	 */
	public Oscilloscope() {
		this(DEFAULT_NAME);
	}

	/**
	 * @param name oscilloscope name
	 */
	public Oscilloscope(String name) {

		super(name);

		input = addPrimaryInput(name + "_input");

		var oscilloscopeSampleCount = Settings.INSTANCE.frameRate();
		buffer = new CircularFloatArray(oscilloscopeSampleCount);

		invokeLater(() -> new OscilloscopeView(this));

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
	 * Fills the given {@code array} with samples to display.
	 *
	 * @param readArray array where to store read samples
	 */
	public void read(float[] readArray) {

		synchronized (buffer) {
			buffer.readLast(readArray);
		}
	}
}
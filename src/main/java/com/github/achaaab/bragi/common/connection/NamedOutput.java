package com.github.achaaab.bragi.common.connection;

import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * A default implementation for outputs.
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.3
 */
public abstract class NamedOutput implements Output {

	private static final Logger LOGGER = getLogger(NamedOutput.class);

	protected final String name;
	protected final List<Buffer> buffers;

	/**
	 * Create an output, initially not connected.
	 *
	 * @param name name of the output to create
	 */
	public NamedOutput(String name) {

		this.name = name;

		buffers = new ArrayList<>();
	}

	@Override
	public boolean isConnected() {
		return !buffers.isEmpty();
	}

	@Override
	public void connect(Input input) {

		var buffer = new Buffer(this, input);

		input.setBuffer(buffer);

		synchronized (buffers) {

			buffers.add(buffer);
			buffers.notifyAll();
		}

		LOGGER.info(this + " connected to " + input);
	}

	@Override
	public String toString() {
		return name;
	}
}
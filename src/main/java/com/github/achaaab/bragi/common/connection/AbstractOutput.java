package com.github.achaaab.bragi.common.connection;

import com.github.achaaab.bragi.common.AbstractNamedEntity;
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
public abstract class AbstractOutput extends AbstractNamedEntity implements Output {

	private static final Logger LOGGER = getLogger(AbstractOutput.class);

	protected final List<Buffer> buffers;

	/**
	 * Create an output, initially not connected.
	 *
	 * @param name name of the output to create
	 */
	public AbstractOutput(String name) {

		super(name);

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
	public void disconnect(Buffer buffer) {

		synchronized (buffers) {
			buffers.remove(buffer);
		}
	}

	@Override
	public String toString() {
		return name;
	}
}
package com.github.achaaab.bragi.core.connection;

import com.github.achaaab.bragi.common.AbstractNamedEntity;
import com.github.achaaab.bragi.core.module.Module;
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

	protected final Module module;
	protected final List<Buffer> buffers;

	/**
	 * Create an output, initially not connected.
	 *
	 * @param module module that will contain the created output
	 * @param name   name of the output to create
	 * @since 0.1.8
	 */
	public AbstractOutput(Module module, String name) {

		super(name);

		this.module = module;

		buffers = new ArrayList<>();
	}

	@Override
	public Module module() {
		return module;
	}

	@Override
	public List<Buffer> buffers() {
		return buffers;
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
}
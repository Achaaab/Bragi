package com.github.achaaab.bragi.core.connection;

import com.github.achaaab.bragi.common.AbstractNamedEntity;
import com.github.achaaab.bragi.core.module.Module;

/**
 * A default implementation for inputs.
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.1
 */
public abstract class AbstractInput extends AbstractNamedEntity implements Input {

	protected final Module module;

	protected Buffer buffer;

	/**
	 * Create an input, initially not connected.
	 *
	 * @param module module that will contain the created input
	 * @param name name of the input to create
	 * @since 0.1.8
	 */
	public AbstractInput(Module module, String name) {

		super(name);

		this.module = module;

		buffer = null;
	}

	@Override
	public Module module() {
		return module;
	}

	@Override
	public boolean isConnected() {
		return buffer != null;
	}

	@Override
	public Buffer getBuffer() {
		return buffer;
	}

	@Override
	public void setBuffer(Buffer buffer) {

		synchronized (this) {

			this.buffer = buffer;
			notifyAll();
		}
	}

	@Override
	public void disconnect() {
		buffer.disconnect();
	}
}

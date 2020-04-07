package com.github.achaaab.bragi.common.connection;

import com.github.achaaab.bragi.common.AbstractNamedEntity;

/**
 * A default implementation for inputs.
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.1
 */
public abstract class AbstractInput extends AbstractNamedEntity implements Input {

	protected Buffer buffer;

	/**
	 * Create an input, initially not connected.
	 *
	 * @param name name of the input to create
	 */
	public AbstractInput(String name) {

		super(name);

		buffer = null;
	}

	@Override
	public boolean isConnected() {
		return buffer != null;
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

	@Override
	public String toString() {
		return name;
	}
}
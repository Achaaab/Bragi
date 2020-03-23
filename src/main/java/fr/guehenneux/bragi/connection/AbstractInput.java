package fr.guehenneux.bragi.connection;

/**
 * A default implementation for inputs.
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.1
 */
public abstract class AbstractInput implements Input {

	protected String name;
	protected Buffer buffer;

	/**
	 * Create an input, initially not connected.
	 *
	 * @param name name of the input to create
	 */
	public AbstractInput(String name) {

		this.name = name;

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
	public String toString() {
		return name;
	}
}
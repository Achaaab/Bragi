package fr.guehenneux.bragi.connection;

import java.util.concurrent.BlockingQueue;

/**
 * @author Jonathan Guéhenneux
 */
public class Input {

	private String name;
	private BlockingQueue<float[]> buffer;

	/**
	 * @param name input name
	 */
	public Input(String name) {

		this.name = name;
		buffer = null;
	}

	/**
	 * @param buffer buffer to read from
	 */
	public synchronized void setBuffer(BlockingQueue<float[]> buffer) {

		this.buffer = buffer;

		notifyAll();
	}

	/**
	 * @return whether an output port is connected to this input port
	 */
	public boolean isConnected() {
		return buffer != null;
	}

	/**
	 * Waits and returns the next chunk.
	 *
	 * @return read chunk
	 */
	public synchronized float[] read() throws InterruptedException {

		while (!isConnected()) {
			wait();
		}

		return buffer.take();
	}

	/**
	 * Returns the next chunk, or {@code null} if the buffer is empty. Does not wait.
	 *
	 * @return next chunk
	 */
	public synchronized float[] tryRead() {
		return isConnected() ? buffer.poll() : null;
	}

	@Override
	public String toString() {
		return name;
	}
}
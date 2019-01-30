package fr.guehenneux.bragi.connection;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author Jonathan Guéhenneux
 */
public class Input {

	private String name;
	private BlockingQueue<float[]> buffer;

	/**
	 * @param name
	 */
	public Input(String name) {

		this.name = name;
		buffer = null;
	}

	/**
	 * @param buffer
	 */
	public synchronized void setBuffer(BlockingQueue<float[]> buffer) {

		this.buffer = buffer;

		notifyAll();
	}

	/**
	 * @return
	 */
	public BlockingQueue<float[]> getBuffer() {
		return buffer;
	}

	/**
	 * @return
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

		while (buffer == null) {
			wait();
		}

		return buffer.take();
	}

	@Override
	public String toString() {
		return name;
	}
}
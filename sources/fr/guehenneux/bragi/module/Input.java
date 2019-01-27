package fr.guehenneux.bragi.module;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author Jonathan Guéhenneux
 */
public class Input {

	private String name;
	private final BlockingQueue<float[]> buffer;

	/**
	 * @param name
	 */
	public Input(String name) {

		this.name = name;
		buffer = new ArrayBlockingQueue<>(1);
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
	public boolean isReady() {
		return !buffer.isEmpty();
	}

	/**
	 * Waits and returns the next chunk.
	 *
	 * @return read chunk
	 */
	public float[] read() throws InterruptedException {
		return buffer.take();
	}

	@Override
	public String toString() {
		return name;
	}
}
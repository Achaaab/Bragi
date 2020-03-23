package fr.guehenneux.bragi.connection;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author Jonathan Guéhenneux
 * @since 0.0.1
 */
public class Buffer {

	private BlockingQueue<float[]> chunks;

	/**
	 * Create a new empty buffer with a capacity of 1 chunk.
	 */
	public Buffer() {

		chunks = new ArrayBlockingQueue<>(1);
	}

	/**
	 * Write the given chunk in this buffer, waiting if necessary for available space.
	 *
	 * @param chunk chunk to write in this buffer
	 * @throws InterruptedException if interrupted while waiting for available space
	 */
	public void write(float[] chunk) throws InterruptedException {
		chunks.put(chunk);
	}

	/**
	 * Write the given chunk in this buffer, if there is available space.
	 *
	 * @param chunk chunk to try to write in this buffer
	 */
	public void tryWrite(float[] chunk) {
		chunks.add(chunk);
	}

	/**
	 * Read a chunk from this buffer, waiting if necessary until a chunks becomes available.
	 *
	 * @return read chunk
	 * @throws InterruptedException if interrupted while waiting for an available chunk
	 */
	public float[] read() throws InterruptedException {
		return chunks.take();
	}

	/**
	 * Read a chunk from this buffer, if there is an available chunk.
	 *
	 * @return read chunk, if there was an available chunk, {@code null} otherwise
	 */
	public float[] tryRead() {
		return chunks.poll();
	}
}
package com.github.achaaab.bragi.common.connection;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author Jonathan Guéhenneux
 * @since 0.0.1
 */
public class Buffer {

	private Output output;
	private Input input;

	private BlockingQueue<float[]> chunks;

	/**
	 * Create a new empty buffer between specified output and input with a capacity of 1 chunk.
	 *
	 * @param output output that will write to this buffer
	 * @param input input that will read from this buffer
	 */
	public Buffer(Output output, Input input) {

		this.output = output;
		this.input = input;

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

	/**
	 * @return output that write to this buffer
	 */
	public Output getOutput() {
		return output;
	}

	/**
	 * @return input that reads from this buffer
	 */
	public Input getInput() {
		return input;
	}
}
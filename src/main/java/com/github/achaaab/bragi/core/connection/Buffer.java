package com.github.achaaab.bragi.core.connection;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author Jonathan Guéhenneux
 * @since 0.0.1
 */
public class Buffer {

	private final Output output;
	private final Input input;

	private final BlockingQueue<float[]> chunks;

	/**
	 * Create a new empty buffer between specified output and input with a capacity of 1 chunk.
	 *
	 * @param output output that will write to this buffer
	 * @param input input that will read from this buffer
	 * @since 0.2.0
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
	 * @since 0.2.0
	 */
	public void write(float[] chunk) throws InterruptedException {
		chunks.put(chunk);
	}

	/**
	 * Write the given chunk in this buffer, if there is available space.
	 *
	 * @param chunk chunk to try to write in this buffer
	 * @since 0.2.0
	 */
	public void tryWrite(float[] chunk) {
		chunks.add(chunk);
	}

	/**
	 * Read a chunk from this buffer, waiting if necessary until a chunks becomes available.
	 *
	 * @return read chunk
	 * @throws InterruptedException if interrupted while waiting for an available chunk
	 * @since 0.2.0
	 */
	public float[] read() throws InterruptedException {
		return chunks.take();
	}

	/**
	 * Read a chunk from this buffer, if there is an available chunk.
	 *
	 * @return read chunk, if there was an available chunk, {@code null} otherwise
	 * @since 0.2.0
	 */
	public float[] tryRead() {
		return chunks.poll();
	}

	/**
	 * Disconnects the output from the input.
	 *
	 * @since 0.2.0
	 */
	public void disconnect() {

		output.disconnect(this);
		input.setBuffer(null);
	}

	/**
	 * @return output that write to this buffer
	 * @since 0.1.8
	 */
	public Output output() {
		return output;
	}

	/**
	 * @return input that reads from this buffer
	 * @since 0.1.8
	 */
	public Input input() {
		return input;
	}
}

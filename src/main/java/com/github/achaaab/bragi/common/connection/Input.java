package com.github.achaaab.bragi.common.connection;

/**
 * Input of a module.
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.1
 */
public interface Input {

	/**
	 * @param buffer buffer to read from
	 */
	void setBuffer(Buffer buffer);

	/**
	 * @return whether an output port is connected to this input port
	 */
	boolean isConnected();

	/**
	 * Read a chunk from this input.
	 *
	 * @return read chunk, {@code null} if no chunk was read
	 * @throws InterruptedException if interrupted while waiting for an available chunk
	 */
	float[] read() throws InterruptedException;
}
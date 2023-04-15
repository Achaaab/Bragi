package com.github.achaaab.bragi.core.connection;

import com.github.achaaab.bragi.common.NamedEntity;
import com.github.achaaab.bragi.core.module.Module;

/**
 * input of a module
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.1
 */
public interface Input extends NamedEntity {

	/**
	 * @return modules containing this input
	 * @since 0.1.8
	 */
	Module module();

	/**
	 * @return buffer from which this inputs reads samples, {@code null} if this input is not connected
	 * @see #isConnected()
	 * @since 0.1.8
	 */
	Buffer getBuffer();

	/**
	 * @param buffer buffer to read from
	 * @since 0.2.0
	 */
	void setBuffer(Buffer buffer);

	/**
	 * @return whether an output port is connected to this input port
	 * @since 0.2.0
	 */
	boolean isConnected();

	/**
	 * Reads a chunk from this input.
	 *
	 * @return read chunk, {@code null} if no chunk was read
	 * @throws InterruptedException if interrupted while waiting for an available chunk
	 * @since 0.2.0
	 */
	float[] read() throws InterruptedException;

	/**
	 * Disconnects this input from the output.
	 *
	 * @since 0.2.0
	 */
	void disconnect();
}

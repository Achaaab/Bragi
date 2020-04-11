package com.github.achaaab.bragi.core.connection;

import com.github.achaaab.bragi.common.NamedEntity;
import com.github.achaaab.bragi.core.module.Module;

import java.util.List;

/**
 * @author Jonathan Guéhenneux
 * @since 0.1.0
 */
public interface Output extends NamedEntity {

	/**
	 * @return modules containing this output
	 * @since 0.1.8
	 */
	Module module();

	/**
	 * @return buffers to which this output writes
	 * @since 0.1.8
	 */
	List<Buffer> buffers();

	/**
	 * Connect this output to the specified input.
	 *
	 * @param input input to connect to
	 */
	void connect(Input input);

	/**
	 * @return whether this output is connected to at least 1 input
	 */
	boolean isConnected();

	/**
	 * Write a chunk to this output. Depending on the implementation, it is not guaranteed that the chunk will be
	 * written.
	 *
	 * @param chunk chunk to write
	 * @throws InterruptedException if interrupted while writing chunk
	 */
	void write(float[] chunk) throws InterruptedException;

	/**
	 * Disconnects this output from the input connected through the given buffer.
	 *
	 * @param buffer buffer connecting this output to an input
	 */
	void disconnect(Buffer buffer);
}
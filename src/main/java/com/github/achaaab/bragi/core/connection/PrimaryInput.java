package com.github.achaaab.bragi.core.connection;

import com.github.achaaab.bragi.core.module.Module;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * A primary input must always provide a chunk when read.
 * If necessary, it will wait for connection.
 * If necessary it will wait for an available chunk.
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.1
 */
public class PrimaryInput extends AbstractInput {

	private static final Logger LOGGER = getLogger(PrimaryInput.class);

	/**
	 * Create a primary input, initially not connected.
	 *
	 * @param module module that will contain the created input
	 * @param name name of the primary input to create
	 * @since 0.1.8
	 */
	public PrimaryInput(Module module, String name) {
		super(module, name);
	}

	/**
	 * Waits and returns the next chunk.
	 *
	 * @return read chunk
	 * @throws InterruptedException if interrupted while waiting for a chunk
	 * @since 0.2.0
	 */
	public float[] read() throws InterruptedException {

		synchronized (this) {

			while (!isConnected()) {

				LOGGER.debug("waiting for a connection to {}", this);
				wait();
			}
		}

		LOGGER.debug("reading chunk from {} to {}", buffer.output(), this);
		var chunk = buffer.read();
		LOGGER.debug("chunk read from {} to {}", buffer.output(), this);

		return chunk;
	}
}

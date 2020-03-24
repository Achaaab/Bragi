package fr.guehenneux.bragi.connection;

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
	 * @param name name of the primary input to create
	 */
	public PrimaryInput(String name) {
		super(name);
	}

	/**
	 * Waits and returns the next chunk.
	 *
	 * @return read chunk
	 * @throws InterruptedException if interrupted while waiting for a chunk
	 */
	public float[] read() throws InterruptedException {

		synchronized (this) {
			while (!isConnected()) {
				wait();
			}
		}

		var chunk = buffer.read();
		LOGGER.debug("read chunk from " + this);

		return chunk;
	}
}
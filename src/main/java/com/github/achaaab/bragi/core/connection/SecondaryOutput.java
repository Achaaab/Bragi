package com.github.achaaab.bragi.core.connection;

import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * A secondary output write chunks only when connected to at least 1 input.
 * It will never wait for a connection.
 * If necessary it will wait for available space in buffers.
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.3
 */
public class SecondaryOutput extends AbstractOutput {

	private static final Logger LOGGER = getLogger(SecondaryOutput.class);

	/**
	 * Create a secondary output, initially not connected.
	 *
	 * @param name name of the secondary output to create
	 */
	public SecondaryOutput(String name) {
		super(name);
	}

	@Override
	public void write(float[] chunk) throws InterruptedException {

		synchronized (buffers) {

			if (isConnected()) {

				for (var buffer : buffers) {

					LOGGER.debug("writing chunk from {} to {}", this, buffer.getInput());
					buffer.write(chunk);
					LOGGER.debug("chunk written from {} to {}", this, buffer.getInput());
				}
			}
		}
	}
}

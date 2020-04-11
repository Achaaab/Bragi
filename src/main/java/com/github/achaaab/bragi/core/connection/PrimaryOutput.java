package com.github.achaaab.bragi.core.connection;

import com.github.achaaab.bragi.core.module.Module;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * A primary output must write chunks.
 * If necessary, it will wait for connection.
 * If necessary it will wait for available space in buffers.
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.3
 */
public class PrimaryOutput extends AbstractOutput {

	private static final Logger LOGGER = getLogger(PrimaryOutput.class);

	/**
	 * Create a primary output, initially not connected.
	 *
	 * @param module module that will contain the created output
	 * @param name   name of the primary output to create
	 * @since 0.1.8
	 */
	public PrimaryOutput(Module module, String name) {
		super(module, name);
	}

	@Override
	public void write(float[] chunk) throws InterruptedException {

		synchronized (buffers) {

			while (!isConnected()) {

				LOGGER.debug("waiting for a connection from {}", this);
				buffers.wait();
			}

			for (var buffer : buffers) {

				LOGGER.debug("writing chunk from {} to {}", this, buffer.input());
				buffer.write(chunk);
				LOGGER.debug("chunk written from {} to {}", this, buffer.input());
			}
		}
	}
}
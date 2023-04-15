package com.github.achaaab.bragi.core.connection;

import com.github.achaaab.bragi.core.module.Module;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * A secondary input may not be connected but still can be read, in this case a {@code null} chunk is returned.
 * A secondary input may not provide a chunk at first read after the connection.
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.1
 */
public class SecondaryInput extends AbstractInput {

	private static final Logger LOGGER = getLogger(SecondaryInput.class);

	private boolean firstRead;

	/**
	 * Create a secondary input, initially not connected.
	 *
	 * @param module module that will contain the created input
	 * @param name name of the secondary input to create
	 * @since 0.1.8
	 */
	public SecondaryInput(Module module, String name) {
		super(module, name);
	}

	/**
	 * @param buffer buffer to read from
	 * @since 0.2.0
	 */
	public void setBuffer(Buffer buffer) {

		firstRead = true;

		super.setBuffer(buffer);
	}

	/**
	 * Wait and returns the next chunk if this input is connected.
	 *
	 * @return next chunk, or {@code null} if this input is not connected
	 * @throws InterruptedException if interrupted while waiting for a chunk
	 * @since 0.2.0
	 */
	public float[] read() throws InterruptedException {

		float[] chunk;

		if (isConnected()) {

			if (firstRead) {

				LOGGER.debug("trying to read chunk from {} to {}", buffer.output(), this);
				chunk = buffer.tryRead();

				if (chunk == null) {

					LOGGER.debug("no chunk read from {} to {}", buffer.output(), this);

				} else {

					LOGGER.debug("chunk read from {} to {}", buffer.output(), this);
					firstRead = false;
				}

			} else {

				LOGGER.debug("reading chunk from {} to {}", buffer.output(), this);
				chunk = buffer.read();
				LOGGER.debug("read chunk from " + this);
			}

		} else {

			chunk = null;
		}

		return chunk;
	}
}

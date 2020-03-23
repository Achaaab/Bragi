package fr.guehenneux.bragi.connection;

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
	 * @param name name of the secondary input to create
	 */
	public SecondaryInput(String name) {
		super(name);
	}

	/**
	 * @param buffer buffer to read from
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
	 */
	public float[] read() throws InterruptedException {

		float[] chunk;

		if (isConnected()) {

			if (firstRead) {

				chunk = buffer.tryRead();
				firstRead = false;

			} else {

				chunk = buffer.read();
			}

			LOGGER.debug("read chunk from " + this);

		} else {

			chunk = null;
		}

		return chunk;
	}
}
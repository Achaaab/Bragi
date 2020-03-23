package fr.guehenneux.bragi.connection;

import fr.guehenneux.bragi.module.model.Module;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Jonathan Guéhenneux
 */
public class Output {

	private static final Logger LOGGER = getLogger(Output.class);

	private String name;
	private final List<Buffer> buffers;

	/**
	 * @param name name of the output
	 */
	public Output(String name) {

		this.name = name;

		buffers = new ArrayList<>();
	}

	/**
	 * @return whether this output is connected to at least 1 input
	 */
	public boolean isConnected() {
		return !buffers.isEmpty();
	}

	/**
	 * First, waits until this output is connected.
	 * Then, writes the given chunk in each buffer (1 buffer per input connected).
	 *
	 * @param chunk chunk to write
	 * @throws InterruptedException if interrupted while waiting for available space in buffers
	 */
	public void write(float[] chunk) throws InterruptedException {

		synchronized (buffers) {

			while (!isConnected()) {
				buffers.wait();
			}

			LOGGER.debug("write chunk to " + this);

			for (var buffer : buffers) {
				buffer.write(chunk);
			}
		}
	}

	/**
	 * If this output is connected, write the given chunk in each buffer (1 buffer per input connected).
	 *
	 * @param chunk optional chunk to write
	 * @throws InterruptedException if interrupted while waiting for available space in buffers
	 */
	public void tryWrite(float[] chunk) throws InterruptedException {

		synchronized (buffers) {

			if (isConnected()) {

				LOGGER.debug("write chunk to " + this);

				for (var buffer : buffers) {
					buffer.write(chunk);
				}
			}
		}
	}

	/**
	 * @param input input to connect
	 */
	public void connect(Input input) {

		var buffer = new Buffer();

		input.setBuffer(buffer);

		synchronized (buffers) {

			buffers.add(buffer);
			buffers.notifyAll();
		}

		LOGGER.info(this + " connected to " + input);
	}

	/**
	 * Connects this output to each input of the given module.
	 *
	 * @param module module to connect to
	 */
	public void connect(Module module) {
		module.getInputs().forEach(this::connect);
	}

	@Override
	public String toString() {
		return name;
	}
}
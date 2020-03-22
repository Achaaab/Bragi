package fr.guehenneux.bragi.connection;

import fr.guehenneux.bragi.module.model.Module;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Jonathan Guéhenneux
 */
public class Output {

	private static final Logger LOGGER = getLogger(Output.class);

	private String name;
	private List<BlockingQueue<float[]>> buffers;

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
	 */
	public synchronized void write(float[] chunk) throws InterruptedException {

		while (!isConnected()) {
			wait();
		}

		for (var buffer : buffers) {
			buffer.put(chunk);
		}
	}

	/**
	 * If this output is connected, try to write the given chunk in each buffer (1 buffer per input connected).
	 *
	 * @param chunk optional chunk to write
	 */
	public synchronized void tryWrite(float[] chunk) {

		if (isConnected()) {

			for (var buffer : buffers) {
				buffer.offer(chunk);
			}
		}
	}

	/**
	 * @param input input to connect
	 */
	public synchronized void connect(Input input) {

		var buffer = new ArrayBlockingQueue<float[]>(1);

		input.setBuffer(buffer);
		buffers.add(buffer);
		notifyAll();

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
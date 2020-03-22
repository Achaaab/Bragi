package fr.guehenneux.bragi.connection;

import fr.guehenneux.bragi.module.model.Module;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author Jonathan Guéhenneux
 */
public class Output {

	private static final Logger LOGGER = LogManager.getLogger();

	private String name;
	private List<BlockingQueue<float[]>> buffers;

	/**
	 * @param name output name
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
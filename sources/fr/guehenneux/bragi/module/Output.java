package fr.guehenneux.bragi.module;

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
	 * @param name
	 */
	public Output(String name) {

		this.name = name;

		buffers = new ArrayList<>();
	}

	/**
	 * @param chunk
	 */
	public synchronized void write(float[] chunk) throws InterruptedException {

		while (buffers.isEmpty()) {
			wait();
		}

		for (BlockingQueue<float[]> buffer : buffers) {
			buffer.put(chunk);
		}
	}

	/**
	 * @param input
	 */
	public synchronized void connect(Input input) {

		BlockingQueue<float[]> buffer = input.getBuffer();
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
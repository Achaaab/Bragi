package fr.guehenneux.bragi.module;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author Jonathan Guéhenneux
 */
public class Output {

	private List<BlockingQueue<float[]>> buffers;

	/**
	 *
	 */
	public Output() {
		buffers = new ArrayList<>();
	}

	/**
	 * @param chunk
	 */
	public synchronized void write(float[] chunk) throws InterruptedException {

		for (BlockingQueue<float[]> buffer : buffers) {
			buffer.put(chunk);
		}
	}

	/**
	 * @return
	 */
	public boolean isConnected() {
		return !buffers.isEmpty();
	}

	/**
	 * @param input
	 */
	public synchronized void connect(Input input) {

		BlockingQueue<float[]> buffer = new ArrayBlockingQueue<>(5);
		buffers.add(buffer);
		input.setBuffer(buffer);
	}
}
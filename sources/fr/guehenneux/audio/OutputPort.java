package fr.guehenneux.audio;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author Jonathan Guéhenneux
 */
public class OutputPort {

	private List<BlockingQueue<float[]>> outputBuffers;

	/**
	 *
	 */
	public OutputPort() {
		outputBuffers = new ArrayList<>();
	}

	/**
	 * @param chunk
	 */
	public synchronized void write(float[] chunk) throws InterruptedException {

		for (BlockingQueue<float[]> outputBuffer : outputBuffers) {
			outputBuffer.put(chunk);
		}
	}

	/**
	 * @return
	 */
	public boolean isConnected() {
		return !outputBuffers.isEmpty();
	}

	/**
	 * @param inputPort
	 */
	public synchronized void connect(InputPort inputPort) {

		BlockingQueue<float[]> buffer = new ArrayBlockingQueue<>(5);
		outputBuffers.add(buffer);
		inputPort.setInputBuffer(buffer);
	}
}
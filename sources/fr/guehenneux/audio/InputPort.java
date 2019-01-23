package fr.guehenneux.audio;

import java.util.concurrent.BlockingQueue;

/**
 * @author Jonathan Guéhenneux
 */
public class InputPort {

	private BlockingQueue<float[]> inputBuffer;

	/**
	 *
	 */
	public InputPort() {

	}

	/**
	 * @param inputBuffer
	 */
	public void setInputBuffer(BlockingQueue<float[]> inputBuffer) {
		this.inputBuffer = inputBuffer;
	}

	/**
	 * @return
	 */
	public boolean isConnected() {
		return inputBuffer != null;
	}

	/**
	 * @return
	 */
	public boolean isReady() {
		return !inputBuffer.isEmpty();
	}

	/**
	 * @return
	 */
	public float[] read() throws InterruptedException {
		return inputBuffer.take();
	}
}
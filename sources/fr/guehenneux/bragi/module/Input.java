package fr.guehenneux.bragi.module;

import java.util.concurrent.BlockingQueue;

/**
 * @author Jonathan Guéhenneux
 */
public class Input {

	private BlockingQueue<float[]> buffer;

	/**
	 * @param buffer
	 */
	public void setBuffer(BlockingQueue<float[]> buffer) {
		this.buffer = buffer;
	}

	/**
	 * @return
	 */
	public boolean isConnected() {
		return buffer != null;
	}

	/**
	 * @return
	 */
	public boolean isReady() {
		return !buffer.isEmpty();
	}

	/**
	 * @return
	 */
	public float[] read() throws InterruptedException {
		return buffer.take();
	}
}
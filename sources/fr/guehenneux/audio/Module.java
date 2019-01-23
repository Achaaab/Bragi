package fr.guehenneux.audio;

import java.util.List;

/**
 * @author Jonathan Guéhenneux
 */
public abstract class Module implements Runnable {

	protected String name;

	/**
	 * @param name
	 */
	public Module(String name) {
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name module name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Start the module in a new thread.
	 */
	public void start() {

		Thread moduleThread = new Thread( this, name);
		moduleThread.start();
	}

	@Override
	public void run() {

		while (true) {

			try {
				compute();
			} catch (InterruptedException cause) {
				throw new RuntimeException(cause);
			}
		}
	}

	/**
	 * @throws InterruptedException if computing was interrupted
	 */
	protected abstract void compute() throws InterruptedException;
}
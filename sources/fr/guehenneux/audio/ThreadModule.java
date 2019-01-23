package fr.guehenneux.audio;

public class ThreadModule extends Thread {

	private Module module;

	/**
	 * @param module
	 */
	public ThreadModule(Module module) {
		this.module = module;
	}

	public void run() {

		while (true) {
			module.compute();
		}
	}
}

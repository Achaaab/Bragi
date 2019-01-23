package fr.guehenneux.audio;

import java.awt.Component;

/**
 * @author Jonathan Guéhenneux
 */
public class PainterThread extends Thread {

	private Component component;
	private int fps;

	/**
	 * @param component
	 * @param fps
	 */
	public PainterThread(Component component, int fps) {

		this.component = component;
		this.fps = fps;
	}

	@Override
	public void run() {

		while (true) {

			component.repaint();

			try {
				sleep(1000 / fps);
			} catch (InterruptedException cause) {
				throw new RuntimeException(cause);
			}
		}
	}
}
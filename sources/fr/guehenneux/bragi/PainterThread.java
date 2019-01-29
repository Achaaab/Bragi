package fr.guehenneux.bragi;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Jonathan Guéhenneux
 */
public class PainterThread extends Thread {

	private static final Logger LOGGER = LogManager.getLogger();

	private JComponent component;
	private int fps;
	private long loopTime;

	/**
	 * @param component
	 * @param fps
	 */
	public PainterThread(JComponent component, int fps) {

		this.component = component;
		this.fps = fps;

		loopTime = Math.round(1_000_000_000 / fps);
		component.setIgnoreRepaint(true);
	}

	@Override
	public void run() {

		long startTime;
		long endTime = System.nanoTime();

		while (true) {

			startTime = endTime;

			try {

				SwingUtilities.invokeAndWait(this::paint);
				endTime = waitLoopTime(startTime);

			} catch (InterruptedException | InvocationTargetException cause) {

				throw new RuntimeException(cause);
			}
		}
	}

	/**
	 * @param startTime
	 *            paint start time
	 * @return loop end time
	 */
	private long waitLoopTime(long startTime) throws InterruptedException {

		long loopEndTime;
		long endTime = System.nanoTime();
		long paintTime = endTime - startTime;

		if (paintTime < loopTime) {

			long waitTime = (loopTime - paintTime) / 1_000_000;
			Thread.sleep(waitTime);
			loopEndTime = System.nanoTime();

		} else {

			LOGGER.warn("unable to reach required frame rate");
			loopEndTime = endTime;
		}

		return loopEndTime;
	}

	/**
	 *
	 */
	private void paint() {
		component.paintImmediately(component.getBounds());
	}
}
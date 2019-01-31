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
	 * @param component component to repaint
	 * @param fps required number of frames per second
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

			if (true || paintTime > 10 * loopTime){
				LOGGER.warn("frame rate drops under 10% of requirement");
			}

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
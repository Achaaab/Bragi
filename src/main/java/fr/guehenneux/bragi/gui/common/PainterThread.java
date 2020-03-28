package fr.guehenneux.bragi.gui.common;

import org.slf4j.Logger;

import javax.swing.JComponent;
import java.lang.reflect.InvocationTargetException;

import static java.lang.Math.round;
import static java.lang.System.nanoTime;
import static javax.swing.SwingUtilities.invokeAndWait;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Jonathan Guéhenneux
 * @since 0.0.6
 */
public class PainterThread extends Thread {

	private static final Logger LOGGER = getLogger(PainterThread.class);

	private JComponent component;
	private long loopTime;

	/**
	 * @param component component to repaint
	 * @param fps       required number of frames per second
	 */
	public PainterThread(JComponent component, int fps) {

		this.component = component;

		loopTime = round(1_000_000_000.0 / fps);
		component.setIgnoreRepaint(true);
	}

	@Override
	public void run() {

		long startTime;
		var endTime = nanoTime();

		while (true) {

			startTime = endTime;

			try {

				invokeAndWait(this::paint);
				endTime = waitLoopTime(startTime);

			} catch (InterruptedException | InvocationTargetException cause) {

				throw new RuntimeException(cause);
			}
		}
	}

	/**
	 * @param startTime paint start time
	 * @return loop end time
	 */
	private long waitLoopTime(long startTime) throws InterruptedException {

		long loopEndTime;

		var endTime = nanoTime();
		var paintTime = endTime - startTime;

		if (paintTime < loopTime) {

			var waitTime = (loopTime - paintTime) / 1_000_000;
			sleep(waitTime);
			loopEndTime = nanoTime();

		} else {

			if (paintTime > 10 * loopTime) {
				LOGGER.warn("frame rate drops under 10% of requirement");
			}

			loopEndTime = endTime;
		}

		return loopEndTime;
	}

	/**
	 * Paints the component.
	 */
	private void paint() {
		component.paintImmediately(component.getBounds());
	}
}
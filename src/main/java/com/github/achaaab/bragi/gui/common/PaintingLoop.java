package com.github.achaaab.bragi.gui.common;

import org.slf4j.Logger;

import javax.swing.JComponent;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayDeque;
import java.util.Queue;

import static java.awt.Toolkit.getDefaultToolkit;
import static java.lang.Math.round;
import static java.lang.System.nanoTime;
import static java.lang.Thread.sleep;
import static javax.swing.SwingUtilities.invokeAndWait;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Jonathan Guéhenneux
 * @since 0.0.6
 */
public class PaintingLoop implements Runnable {

	private static final Logger LOGGER = getLogger(PaintingLoop.class);

	private static final Toolkit TOOLKIT = getDefaultToolkit();

	private static final int NANOSECONDS_PER_MILLISECOND = 1_000_000;
	private static final double NANOSECONDS_PER_SECOND = 1_000_000_000.0;

	/**
	 * number of frame times to average in order to get the mean frame rate
	 */
	private static final int SAMPLE_COUNT = 60;

	private final JComponent component;

	private final double targetFrameRate;
	private final long targetFrameTime;
	private final Queue<Long> frameTimes;

	private boolean running;
	private long totalFrameTimes;
	private double frameRate;

	/**
	 * @param component       component to paint
	 * @param targetFrameRate required number of frames per second
	 */
	public PaintingLoop(JComponent component, double targetFrameRate) {

		this.component = component;
		this.targetFrameRate = targetFrameRate;

		component.setIgnoreRepaint(true);

		targetFrameTime = round(NANOSECONDS_PER_SECOND / targetFrameRate);

		frameTimes = new ArrayDeque<>();
		totalFrameTimes = 0;

		new Thread(this).start();
	}

	@Override
	public void run() {

		running = true;

		var start = nanoTime();
		var end = start;

		while (running) {

			start = end;

			try {

				invokeAndWait(this::paint);
				end = waitTargetFrameTime(start);

			} catch (InterruptedException | InvocationTargetException cause) {

				throw new RuntimeException(cause);
			}
		}
	}

	/**
	 * Stops this painting loop.
	 *
	 * @since 0.1.6
	 */
	public void stop() {
		running = false;
	}

	/**
	 * @param start paint start time
	 * @return loop end time
	 */
	private long waitTargetFrameTime(long start) throws InterruptedException {

		var end = nanoTime();
		var frameTime = end - start;
		updateFrameRate(frameTime);

		var timeLeft = targetFrameTime - frameTime;

		if (timeLeft > 0) {

			sleep(timeLeft / NANOSECONDS_PER_MILLISECOND);
			end = nanoTime();
		}

		return end;
	}

	/**
	 * Paints the component synchronously.
	 */
	private void paint() {

		component.paintImmediately(component.getBounds());

		/*
		When painting is very fast (observed with an nearly empty spectrum analyser
		which takes less than 1 millisecond to paint). X11 server seems to drop nearly all the frames, it only
		shows 1 frame every 30 rendered frames (estimation). Using this sync() method seems to solve the problem
		at the cost of a significantly increased frame render time.
		*/

		TOOLKIT.sync();
	}

	/**
	 * The frame rate is limited but using the paint time, we can infer a theoretical frame rate.
	 *
	 * @param frameTime frame time in nanoseconds
	 * @since 0.1.6
	 */
	private void updateFrameRate(long frameTime) {

		if (frameTimes.size() == SAMPLE_COUNT) {
			totalFrameTimes -= frameTimes.remove();
		}

		frameTimes.add(frameTime);
		totalFrameTimes += frameTime;

		var frameCount = frameTimes.size();
		frameRate = NANOSECONDS_PER_SECOND / totalFrameTimes * frameCount;

		if (frameTimes.size() == SAMPLE_COUNT && frameRate < 0.1 * targetFrameRate) {
			LOGGER.warn("frame rate ({}) < 10% of target frame rate ({})", frameRate, targetFrameRate);
		}
	}

	/**
	 * @return theoretical frame rate, that could be reached based on last paint times
	 * @since 0.1.6
	 */
	public double getFrameRate() {
		return frameRate;
	}
}
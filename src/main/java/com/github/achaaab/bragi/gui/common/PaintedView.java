package com.github.achaaab.bragi.gui.common;

import javax.swing.JComponent;
import java.awt.Graphics2D;
import java.awt.Point;

import static java.awt.Color.RED;
import static java.lang.Math.round;

/**
 * @author Jonathan Guéhenneux
 * @since 0.1.6
 */
public abstract class PaintedView extends JComponent {

	protected static final double DEFAULT_TARGET_FRAME_RATE = 60.0;

	private static final float FPS_MESSAGE_FONT_SIZE = 30.0f;
	private static final Point FPS_MESSAGE_POSITION = new Point(10, 10);

	protected final PaintingLoop paintingLoop;
	protected final double targetFrameTime;

	/**
	 * Creates a buffered view with the default target frame rate.
	 *
	 * @see #DEFAULT_TARGET_FRAME_RATE
	 */
	protected PaintedView() {
		this(DEFAULT_TARGET_FRAME_RATE);
	}

	/**
	 * Creates a buffered view with the specified target frame rate.
	 *
	 * @param targetFrameRate required number of frames to display per second
	 */
	protected PaintedView(double targetFrameRate) {

		targetFrameTime = 1 / targetFrameRate;
		paintingLoop = new PaintingLoop(this, targetFrameRate);
	}

	/**
	 * Draws a message indicating the average frame rate.
	 *
	 * @param graphics graphics context in which to draw the frame rate message
	 */
	protected void drawFrameRate(Graphics2D graphics) {

		var frameRate = paintingLoop.getFrameRate();
		var frameRateMessage = round(frameRate) + " fps";

		var font = graphics.getFont().deriveFont(FPS_MESSAGE_FONT_SIZE);
		var fontRenderContext = graphics.getFontRenderContext();
		var glyphVector = font.createGlyphVector(fontRenderContext, frameRateMessage);
		var messageBounds = glyphVector.getPixelBounds(null, 0, 0);

		graphics.setFont(font);
		graphics.setColor(RED);

		graphics.drawString(
				frameRateMessage,
				FPS_MESSAGE_POSITION.x,
				FPS_MESSAGE_POSITION.y + messageBounds.height);
	}
}
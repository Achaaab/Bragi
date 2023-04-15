package com.github.achaaab.bragi.gui.common;

import javax.swing.JComponent;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

import static com.github.achaaab.bragi.gui.common.ViewScale.scale;
import static java.awt.Color.RED;
import static java.lang.Math.round;

/**
 * @author Jonathan Guéhenneux
 * @since 0.1.6
 */
public abstract class PaintedView extends JComponent {

	protected static final double DEFAULT_TARGET_FRAME_RATE = 60.0;

	private static final Point FPS_MESSAGE_POSITION = scale(new Point(7, 7));
	private static final float FPS_MESSAGE_FONT_SIZE = scale(12.0f);
	private static final Color FPS_MESSAGE_COLOR = RED;

	protected final PaintingLoop paintingLoop;
	protected final double targetFrameTime;

	/**
	 * Creates a buffered view with the default target frame rate.
	 *
	 * @see #DEFAULT_TARGET_FRAME_RATE
	 * @since 0.2.0
	 */
	protected PaintedView() {
		this(DEFAULT_TARGET_FRAME_RATE);
	}

	/**
	 * Creates a buffered view with the specified target frame rate.
	 *
	 * @param targetFrameRate required number of frames to display per second
	 * @since 0.2.0
	 */
	protected PaintedView(double targetFrameRate) {

		targetFrameTime = 1 / targetFrameRate;
		paintingLoop = new PaintingLoop(this, targetFrameRate);
	}

	/**
	 * Draws a message indicating the average frame rate.
	 *
	 * @param graphics graphics context in which to draw the frame rate message
	 * @since 0.2.0
	 */
	protected void drawFrameRate(Graphics2D graphics) {

		var frameRate = paintingLoop.getFrameRate();
		var frameRateMessage = round(frameRate) + " fps";

		var previousFont = graphics.getFont();

		var fpsFont = previousFont.deriveFont(FPS_MESSAGE_FONT_SIZE);
		graphics.setFont(fpsFont);
		graphics.setColor(FPS_MESSAGE_COLOR);

		var fontRenderContext = graphics.getFontRenderContext();
		var glyphVector = fpsFont.createGlyphVector(fontRenderContext, frameRateMessage);
		var messageBounds = glyphVector.getPixelBounds(null, 0, 0);

		graphics.drawString(
				frameRateMessage,
				FPS_MESSAGE_POSITION.x,
				FPS_MESSAGE_POSITION.y + messageBounds.height);

		// restore previous font
		graphics.setFont(previousFont);
	}
}

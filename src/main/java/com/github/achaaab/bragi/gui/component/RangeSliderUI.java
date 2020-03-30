package com.github.achaaab.bragi.gui.component;

import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.round;
import static javax.swing.SwingConstants.HORIZONTAL;
import static javax.swing.SwingConstants.VERTICAL;
import static javax.swing.SwingUtilities.computeUnion;

/**
 * UI delegate for the RangeSlider component.
 * RangeSliderUI paints two thumbs, one for the lower value and one for the upper value.
 *
 * @author Ernest Yu
 * @author Jonathan Guéhenneux
 * @since 0.1.3
 */
class RangeSliderUI extends BasicSliderUI {

	private static final Color THUMB_COLOR = new Color(176, 192, 208);
	private static final Color RANGE_COLOR = new Color(0, 173, 182);
	private static final double RANGE_THICKNESS = 4.0;
	private static final Shape THUMB_SHAPE = new RoundRectangle2D.Double(0.0, 0.0, 11, 11, 2.0, 2.0);

	private final RangeSlider rangeSlider;

	private Rectangle lowerThumbRect;
	private Rectangle upperThumbRect;

	private boolean upperThumbSelected;

	private transient boolean lowerDragging;
	private transient boolean upperDragging;

	/**
	 * Creates a range slider UI.
	 *
	 * @param rangeSlider range slider to display
	 */
	protected RangeSliderUI(RangeSlider rangeSlider) {

		super(rangeSlider);

		this.rangeSlider = rangeSlider;
	}

	@Override
	public void installUI(JComponent component) {

		upperThumbRect = new Rectangle();

		super.installUI(component);

		lowerThumbRect = thumbRect;
	}

	@Override
	protected TrackListener createTrackListener(JSlider slider) {
		return new RangeTrackListener();
	}

	@Override
	protected ChangeListener createChangeListener(JSlider slider) {
		return new ChangeHandler();
	}

	/**
	 * Updates the dimensions for both thumbs.
	 */
	@Override
	protected void calculateThumbSize() {

		// Call superclass method for lower thumb size.
		super.calculateThumbSize();

		// Set upper thumb size.
		upperThumbRect.setSize(thumbRect.width, thumbRect.height);
	}

	/**
	 * Updates the locations for both thumbs.
	 */
	@Override
	protected void calculateThumbLocation() {

		// Call superclass method for lower thumb location.
		super.calculateThumbLocation();

		// Adjust upper value to snap to ticks if necessary.
		if (slider.getSnapToTicks()) {

			var minimum = rangeSlider.getMinimum();
			var majorTickSpacing = rangeSlider.getMajorTickSpacing();
			var minorTickSpacing = rangeSlider.getMinorTickSpacing();
			var upperValue = rangeSlider.getUpperValue();

			var tickSpacing = min(majorTickSpacing, minorTickSpacing);

			if (tickSpacing != 0) {

				// If it's not on a tick, change the value.
				if ((upperValue - minimum) % tickSpacing != 0) {

					var nearestTick = round((float) (upperValue - minimum) / tickSpacing);
					rangeSlider.setUpperValue(minimum + nearestTick * tickSpacing);
				}
			}
		}

		// Calculate upper thumb location. The thumb is centered over its value on the track.

		switch (slider.getOrientation()) {

			case HORIZONTAL -> {

				var upperPosition = xPositionForValue(rangeSlider.getUpperValue());
				upperThumbRect.x = upperPosition - (upperThumbRect.width / 2);
				upperThumbRect.y = trackRect.y;
			}

			case VERTICAL -> {

				var upperPosition = yPositionForValue(rangeSlider.getUpperValue());
				upperThumbRect.x = trackRect.x;
				upperThumbRect.y = upperPosition - (upperThumbRect.height / 2);
			}
		}
	}

	@Override
	protected Dimension getThumbSize() {
		return new Dimension(12, 12);
	}

	@Override
	public void paint(Graphics graphics, JComponent component) {

		super.paint(graphics, component);

		var clipBounds = graphics.getClipBounds();

		if (upperThumbSelected) {

			// Paint lower thumb first, then upper thumb.

			if (clipBounds.intersects(lowerThumbRect)) {
				paintThumb(graphics, lowerThumbRect);
			}

			if (clipBounds.intersects(upperThumbRect)) {
				paintThumb(graphics, upperThumbRect);
			}

		} else {

			// Paint upper thumb first, then lower thumb.

			if (clipBounds.intersects(upperThumbRect)) {
				paintThumb(graphics, upperThumbRect);
			}

			if (clipBounds.intersects(lowerThumbRect)) {
				paintThumb(graphics, lowerThumbRect);
			}
		}
	}

	@Override
	public void paintTrack(Graphics graphics) {

		super.paintTrack(graphics);

		var graphics2D = (Graphics2D) graphics;
		graphics2D.setColor(RANGE_COLOR);

		switch (slider.getOrientation()) {

			case HORIZONTAL -> {

				var lowerX = lowerThumbRect.getCenterX();
				var upperX = upperThumbRect.getCenterX();
				var lowerY = trackRect.y + (trackRect.height - RANGE_THICKNESS) / 2;
				var rangeWidth = upperX - lowerX;

				graphics2D.fill(new Rectangle2D.Double(lowerX, lowerY, rangeWidth, RANGE_THICKNESS));
			}

			case VERTICAL -> {

				var lowerY = lowerThumbRect.getCenterY();
				var upperY = upperThumbRect.getCenterY();
				var lowerX = trackRect.x + (trackRect.width - RANGE_THICKNESS) / 2;
				var rangeHeight = upperY - lowerY;

				graphics2D.fill(new Rectangle2D.Double(lowerX, lowerY, RANGE_THICKNESS, rangeHeight));
			}
		}
	}

	/**
	 * Overrides superclass method to do nothing.
	 * Thumb painting is handled within the {@link #paint(Graphics, JComponent)} method.
	 */
	@Override
	public void paintThumb(Graphics graphics) {

	}

	/**
	 * Paints a thumb in the specified bounds, using the specified graphics context.
	 *
	 * @param graphics graphics context in which to paint
	 * @param bounds   bounds of the thumb to paint
	 */
	private void paintThumb(Graphics graphics, Rectangle bounds) {

		var graphics2D = (Graphics2D) graphics.create();

		graphics2D.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
		graphics2D.translate(bounds.x, bounds.y);

		graphics2D.setColor(THUMB_COLOR);
		graphics2D.fill(THUMB_SHAPE);

		graphics2D.setColor(THUMB_COLOR.darker());
		graphics2D.draw(THUMB_SHAPE);
	}

	/**
	 * Sets the location of the lower thumb, and repaints the slider. This is called when the lower thumb is dragged
	 * to repaint the slider.
	 *
	 * @param x x coordinate of the lower thumb location
	 * @param y y coordinate of the lower thumb location
	 */
	private void setLowerThumbLocation(int x, int y) {
		setThumbLocation(x, y);
	}

	/**
	 * Sets the location of the upper thumb, and repaints the slider. This is called when the upper thumb is dragged
	 * to repaint the slider.
	 *
	 * @param x x coordinate of the upper thumb location
	 * @param y y coordinate of the upper thumb location
	 */
	private void setUpperThumbLocation(int x, int y) {

		var upperUnionRect = new Rectangle(upperThumbRect);

		upperThumbRect.setLocation(x, y);

		computeUnion(upperThumbRect.x, upperThumbRect.y, upperThumbRect.width, upperThumbRect.height, upperUnionRect);
		slider.repaint(upperUnionRect.x, upperUnionRect.y, upperUnionRect.width, upperUnionRect.height);
	}

	/**
	 * Moves the selected thumb in the specified direction by a block increment.
	 * This method is called when the user presses the Page Up or Down keys.
	 */
	public void scrollByBlock(int direction) {

		synchronized (slider) {

			int blockIncrement = (slider.getMaximum() - slider.getMinimum()) / 10;

			if (blockIncrement <= 0 && slider.getMaximum() > slider.getMinimum()) {
				blockIncrement = 1;
			}

			int delta = blockIncrement * ((direction > 0) ? POSITIVE_SCROLL : NEGATIVE_SCROLL);

			if (upperThumbSelected) {

				int oldValue = ((RangeSlider) slider).getUpperValue();
				((RangeSlider) slider).setUpperValue(oldValue + delta);

			} else {

				int oldValue = slider.getValue();
				slider.setValue(oldValue + delta);
			}
		}
	}

	@Override
	public void scrollByUnit(int direction) {

		synchronized (slider) {

			var delta = direction > 0 ? POSITIVE_SCROLL : NEGATIVE_SCROLL;

			if (upperThumbSelected) {

				var upperValue = rangeSlider.getUpperValue();
				rangeSlider.setUpperValue(upperValue + delta);

			} else {

				var lowerValue = rangeSlider.getLowerValue();
				slider.setValue(lowerValue + delta);
			}
		}
	}

	/**
	 * Listener to handle model change events. This calculates the thumb locations and repaints the slider
	 * if the value change is not caused by dragging a thumb.
	 */
	public class ChangeHandler implements ChangeListener {

		@Override
		public void stateChanged(ChangeEvent event) {

			if (!lowerDragging && !upperDragging) {

				calculateThumbLocation();
				slider.repaint();
			}
		}
	}

	/**
	 * Listener to handle mouse movements in the slider track.
	 */
	public class RangeTrackListener extends TrackListener {

		@Override
		public void mousePressed(MouseEvent event) {

			if (slider.isEnabled()) {

				currentMouseX = event.getX();
				currentMouseY = event.getY();

				if (slider.isRequestFocusEnabled()) {
					slider.requestFocus();
				}

				/*
				Determine which thumb is pressed. If the upper thumb is selected (last one dragged),
				then check its position first; otherwise check the position of the lower thumb first.
				 */

				var lowerPressed = false;
				var upperPressed = false;

				if (upperThumbSelected || slider.getMinimum() == slider.getValue()) {

					if (upperThumbRect.contains(currentMouseX, currentMouseY)) {
						upperPressed = true;
					} else if (lowerThumbRect.contains(currentMouseX, currentMouseY)) {
						lowerPressed = true;
					}

				} else {

					if (lowerThumbRect.contains(currentMouseX, currentMouseY)) {
						lowerPressed = true;
					} else if (upperThumbRect.contains(currentMouseX, currentMouseY)) {
						upperPressed = true;
					}
				}

				if (lowerPressed) {

					switch (slider.getOrientation()) {
						case VERTICAL -> offset = currentMouseY - lowerThumbRect.y;
						case HORIZONTAL -> offset = currentMouseX - lowerThumbRect.x;
					}

					upperThumbSelected = false;
					lowerDragging = true;
					upperDragging = false;

				} else if (upperPressed) {

					switch (slider.getOrientation()) {
						case VERTICAL -> offset = currentMouseY - upperThumbRect.y;
						case HORIZONTAL -> offset = currentMouseX - upperThumbRect.x;
					}

					upperThumbSelected = true;
					upperDragging = true;
					lowerDragging = false;
				}
			}
		}

		@Override
		public void mouseReleased(MouseEvent event) {

			lowerDragging = false;
			upperDragging = false;
			slider.setValueIsAdjusting(false);

			super.mouseReleased(event);
		}

		@Override
		public void mouseDragged(MouseEvent event) {

			if (slider.isEnabled()) {

				currentMouseX = event.getX();
				currentMouseY = event.getY();

				if (lowerDragging) {

					slider.setValueIsAdjusting(true);
					moveLowerThumb();

				} else if (upperDragging) {

					slider.setValueIsAdjusting(true);
					moveUpperThumb();
				}
			}
		}

		@Override
		public boolean shouldScroll(int direction) {
			return false;
		}

		/**
		 * Moves the location of the lower thumb, and sets its corresponding value in the slider.
		 */
		private void moveLowerThumb() {

			switch (slider.getOrientation()) {

				case VERTICAL -> {

					int halfThumbHeight = lowerThumbRect.height / 2;
					int thumbTop = currentMouseY - offset;
					int trackTop = trackRect.y;
					int trackBottom = trackRect.y + trackRect.height - 1;
					int vMax = yPositionForValue(rangeSlider.getUpperValue());

					if (drawInverted()) {
						trackBottom = vMax;
					} else {
						trackTop = vMax;
					}

					thumbTop = max(thumbTop, trackTop - halfThumbHeight);
					thumbTop = min(thumbTop, trackBottom - halfThumbHeight);
					setLowerThumbLocation(lowerThumbRect.x, thumbTop);
					var thumbMiddle = thumbTop + halfThumbHeight;
					slider.setValue(valueForYPosition(thumbMiddle));
				}

				case HORIZONTAL -> {

					int halfThumbWidth = lowerThumbRect.width / 2;
					int thumbLeft = currentMouseX - offset;
					int trackLeft = trackRect.x;
					int trackRight = trackRect.x + trackRect.width - 1;
					int hMax = xPositionForValue(rangeSlider.getUpperValue());

					if (drawInverted()) {
						trackLeft = hMax;
					} else {
						trackRight = hMax;
					}

					thumbLeft = max(thumbLeft, trackLeft - halfThumbWidth);
					thumbLeft = min(thumbLeft, trackRight - halfThumbWidth);
					setLowerThumbLocation(thumbLeft, lowerThumbRect.y);
					var thumbMiddle = thumbLeft + halfThumbWidth;
					slider.setValue(valueForXPosition(thumbMiddle));
				}
			}
		}

		/**
		 * Moves the location of the upper thumb, and sets its corresponding value in the slider.
		 */
		private void moveUpperThumb() {

			switch (slider.getOrientation()) {

				case VERTICAL -> {

					var halfThumbHeight = upperThumbRect.height / 2;
					var thumbTop = currentMouseY - offset;
					var trackTop = trackRect.y;
					var trackBottom = trackRect.y + trackRect.height - 1;
					var vMin = yPositionForValue(slider.getValue());

					if (drawInverted()) {
						trackTop = vMin;
					} else {
						trackBottom = vMin;
					}

					thumbTop = max(thumbTop, trackTop - halfThumbHeight);
					thumbTop = min(thumbTop, trackBottom - halfThumbHeight);
					setUpperThumbLocation(upperThumbRect.x, thumbTop);
					var thumbMiddle = thumbTop + halfThumbHeight;
					slider.setExtent(valueForYPosition(thumbMiddle) - slider.getValue());
				}

				case HORIZONTAL -> {

					var halfThumbWidth = upperThumbRect.width / 2;
					var thumbLeft = currentMouseX - offset;
					var trackLeft = trackRect.x;
					var trackRight = trackRect.x + trackRect.width - 1;
					var hMin = xPositionForValue(slider.getValue());

					if (drawInverted()) {
						trackRight = hMin;
					} else {
						trackLeft = hMin;
					}

					thumbLeft = max(thumbLeft, trackLeft - halfThumbWidth);
					thumbLeft = min(thumbLeft, trackRight - halfThumbWidth);
					setUpperThumbLocation(thumbLeft, upperThumbRect.y);
					var thumbMiddle = thumbLeft + halfThumbWidth;
					slider.setExtent(valueForXPosition(thumbMiddle) - slider.getValue());
				}
			}
		}
	}
}
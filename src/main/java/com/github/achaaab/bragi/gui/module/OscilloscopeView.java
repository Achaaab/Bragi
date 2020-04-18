package com.github.achaaab.bragi.gui.module;

import com.github.achaaab.bragi.common.Settings;
import com.github.achaaab.bragi.core.module.consumer.Oscilloscope;
import com.github.achaaab.bragi.gui.common.PaintedView;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import static com.github.achaaab.bragi.gui.common.ViewScale.scale;
import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_OFF;
import static java.lang.Math.round;

/**
 * oscilloscope Swing view
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.4
 */
public class OscilloscopeView extends PaintedView {

	private static final Dimension SIZE = scale(new Dimension(300, 300));

	private static final int HORIZONTAL_DIVISION_COUNT = 10;
	private static final float VOLTS_PER_DIVISION = 1.0f;

	private static final float MARGIN_RATIO = 0.01f;
	private static final float DIVISION_STROKE_RATIO = 0.001f;
	private static final float SIGNAL_STROKE_RATIO = 0.003f;

	private static final Color SIGNAL_COLOR = new Color(64, 255, 224);
	private static final Color BACKGROUND_COLOR = new Color(5, 93, 108);
	private static final Color DIVISION_COLOR = new Color(32, 32, 32);

	private final Oscilloscope model;

	private final float secondsPerDivision;
	private final int length;
	private final float[] samples;
	private final int[] x;
	private final int[] y;

	/**
	 * @param model oscilloscope model
	 */
	public OscilloscopeView(Oscilloscope model) {

		this.model = model;

		length = Settings.INSTANCE.frameRate() / 60;
		secondsPerDivision = (float) targetFrameTime / HORIZONTAL_DIVISION_COUNT;

		samples = new float[length];
		x = new int[length];
		y = new int[length];

		setPreferredSize(SIZE);
	}

	@Override
	public void paint(Graphics graphics) {

		var width = getWidth();
		var height = getHeight();

		var margin = MARGIN_RATIO * width;
		var divisionStroke = new BasicStroke(DIVISION_STROKE_RATIO * width);
		var signalStroke = new BasicStroke(SIGNAL_STROKE_RATIO * width);

		var plotWidth = width - 2 * margin;
		var plotHeight = height - 2 * margin;

		var graphics2D = (Graphics2D) graphics;
		graphics2D.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_OFF);

		graphics2D.setColor(BACKGROUND_COLOR);
		graphics2D.fillRect(0, 0, width, height);

		graphics2D.setStroke(divisionStroke);
		graphics2D.setPaint(DIVISION_COLOR);

		var divisionSize = plotWidth / HORIZONTAL_DIVISION_COUNT;

		for (var horizontalDivisionIndex = 0; horizontalDivisionIndex <= HORIZONTAL_DIVISION_COUNT; horizontalDivisionIndex++) {

			var divisionX = round(margin + horizontalDivisionIndex * divisionSize);

			graphics2D.drawLine(
					divisionX, round(margin),
					divisionX, round(margin + plotHeight));
		}

		var verticalDivision = 0;

		while (verticalDivision <= plotHeight / 2) {

			var divisionY = round(margin + plotHeight / 2 + verticalDivision);

			graphics2D.drawLine(
					round(margin), divisionY,
					round(margin + plotWidth), divisionY);

			divisionY = round(margin + plotHeight / 2 - verticalDivision);

			graphics2D.drawLine(
					round(margin), divisionY,
					round(margin + plotWidth), divisionY);

			verticalDivision += divisionSize;
		}

		model.read(samples);

		float sampleRate = Settings.INSTANCE.frameRate();

		for (var sampleIndex = 0; sampleIndex < length; sampleIndex++) {

			var sample = samples[sampleIndex];
			var time = sampleIndex / sampleRate;

			x[sampleIndex] = round(margin + divisionSize * time / secondsPerDivision);
			y[sampleIndex] = round(margin + plotHeight / 2.0f - divisionSize * sample / VOLTS_PER_DIVISION);
		}

		graphics2D.setStroke(signalStroke);
		graphics2D.setColor(SIGNAL_COLOR);
		graphics2D.drawPolyline(x, y, length);

		drawFrameRate(graphics2D);
	}
}
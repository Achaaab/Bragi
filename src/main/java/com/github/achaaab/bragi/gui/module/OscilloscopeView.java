package com.github.achaaab.bragi.gui.module;

import com.github.achaaab.bragi.common.Settings;
import com.github.achaaab.bragi.gui.common.PaintedView;
import com.github.achaaab.bragi.module.consumer.Oscilloscope;

import javax.swing.JFrame;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;

import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_OFF;
import static java.lang.Math.round;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

/**
 * oscilloscope Swing view
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.4
 */
public class OscilloscopeView extends PaintedView {

	private static final int MARGIN = 5;
	private static final int HORIZONTAL_DIVISION_COUNT = 10;
	private static final Color PLOT_COLOR = new Color(64, 255, 224);
	private static final Color BACKGROUND_COLOR = new Color(5, 93, 108);
	private static final Color DIVISION_COLOR = new Color(32, 32, 32);
	private static final Stroke PLOT_STROKE = new BasicStroke(3.0f);
	private static final Stroke DIVISION_STROKE = new BasicStroke(1.5f);
	private static final float VOLTS_PER_DIVISION = 1.0f;

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

		setPreferredSize(new Dimension(400, 400));

		var frame = new JFrame(model.name());
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.add(this);
		frame.pack();
		frame.setVisible(true);
	}

	@Override
	public void paint(Graphics graphics) {

		var width = getWidth();
		var height = getHeight();
		var plotWidth = width - 2 * MARGIN;
		var plotHeight = height - 2 * MARGIN;

		var graphics2D = (Graphics2D) graphics;
		graphics2D.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_OFF);

		graphics2D.setColor(BACKGROUND_COLOR);
		graphics2D.fillRect(0, 0, width, height);

		graphics2D.setStroke(DIVISION_STROKE);
		graphics2D.setPaint(DIVISION_COLOR);

		var divisionSize = (float) plotWidth / HORIZONTAL_DIVISION_COUNT;

		for (var horizontalDivisionIndex = 0; horizontalDivisionIndex <= HORIZONTAL_DIVISION_COUNT; horizontalDivisionIndex++) {

			graphics2D.drawLine(
					round(MARGIN + horizontalDivisionIndex * divisionSize), MARGIN,
					round(MARGIN + horizontalDivisionIndex * divisionSize), MARGIN + plotHeight);
		}

		var verticalDivision = 0;

		while (verticalDivision <= plotHeight / 2) {

			graphics2D.drawLine(MARGIN, MARGIN + plotHeight / 2 + verticalDivision,
					MARGIN + plotWidth, MARGIN + plotHeight / 2 + verticalDivision);

			graphics2D.drawLine(MARGIN, MARGIN + plotHeight / 2 - verticalDivision,
					MARGIN + plotWidth, MARGIN + plotHeight / 2 - verticalDivision);

			verticalDivision += divisionSize;
		}

		model.read(samples);

		for (var sampleIndex = 0; sampleIndex < length; sampleIndex++) {

			var sample = samples[sampleIndex];
			var time = (float) sampleIndex / Settings.INSTANCE.frameRate();

			x[sampleIndex] = round(MARGIN + divisionSize * time / secondsPerDivision);
			y[sampleIndex] = round(MARGIN + plotHeight / 2.0f - divisionSize * sample / VOLTS_PER_DIVISION);
		}

		graphics2D.setStroke(PLOT_STROKE);
		graphics2D.setColor(PLOT_COLOR);
		graphics2D.drawPolyline(x, y, length);

		drawFrameRate(graphics2D);
	}
}
package com.github.achaaab.bragi.gui.module;

import com.github.achaaab.bragi.common.Normalizer;
import com.github.achaaab.bragi.gui.common.PaintedView;
import com.github.achaaab.bragi.module.SpectrumAnalyzer;

import javax.swing.JFrame;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import static java.awt.Color.getHSBColor;
import static java.lang.Math.fma;
import static java.lang.Math.log10;
import static java.lang.Math.max;
import static java.lang.Math.round;
import static java.util.Arrays.fill;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

/**
 * A basic spectrum analyzer view made with Swing and AWT.
 * This view is made of vertical bars representing the volume of each band, in decibels.
 * Each bar is made of segments.
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.5
 */
public class SpectrumAnalyzerView extends PaintedView {

	private static final float MARGIN = 5.0f;
	private static final int SEGMENT_COUNT = 128;
	private static final float BASE_MAGNITUDE = 25_000.0f;
	private static final float MINIMAL_DECIBELS = -60.0f;
	private static final float MAXIMAL_DECIBELS = 0.0f;

	/**
	 * force applied to peaks after hanging time, in decibel per second per second (dB/s²)
	 */
	private static final double PEAKS_GRAVITY = 100.0;
	private static final double PEAKS_HANGING_TIME = 0.5;

	private static final Normalizer NORMALIZER = new Normalizer(
			MINIMAL_DECIBELS, MAXIMAL_DECIBELS,
			0, SEGMENT_COUNT
	);

	private static final Color BACKGROUND_COLOR = new Color(24, 24, 24);

	private static final Color[] SEGMENT_COLOR;

	static {

		SEGMENT_COLOR = new Color[SEGMENT_COUNT];

		for (var segmentIndex = 0; segmentIndex < SEGMENT_COUNT; segmentIndex++) {

			SEGMENT_COLOR[segmentIndex] = getHSBColor(
					0.6f - 0.6f * segmentIndex / SEGMENT_COUNT,
					1.0f + 0.0f * segmentIndex / SEGMENT_COUNT,
					1.0f + 0.0f * segmentIndex / SEGMENT_COUNT);
		}
	}

	private final SpectrumAnalyzer model;

	private float[] peaks;
	private double[] peaksTime;
	private double[] peaksSpeed;

	/**
	 * @param model spectrum analyzer model
	 */
	public SpectrumAnalyzerView(SpectrumAnalyzer model) {

		this.model = model;

		setPreferredSize(new Dimension(640, 360));

		var frame = new JFrame(model.getName());
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.add(this);
		frame.pack();
		frame.setVisible(true);
	}

	/**
	 * Ensures peaks, peaks time and peaks speed are created with length = given bar count.
	 *
	 * @param barCount number of bars in the spectrum
	 * @since 0.1.6
	 */
	private void ensurePeaks(int barCount) {

		if (peaks == null || peaks.length != barCount) {

			peaks = new float[barCount];
			fill(peaks, MINIMAL_DECIBELS);

			peaksTime = new double[barCount];
			peaksSpeed = new double[barCount];
		}
	}

	@Override
	public void paint(Graphics graphics) {

		var graphics2D = (Graphics2D) graphics;

		// get averages

		var averages = model.getAverages();
		var barCount = averages.length;
		ensurePeaks(barCount);

		// get spectrum metrics

		var width = getWidth();
		var height = getHeight();

		var spectrumWidth = width - 2 * MARGIN;
		var spectrumHeight = height - 2 * MARGIN;

		var barWidth = spectrumWidth / barCount;
		var rowHeight = spectrumHeight / SEGMENT_COUNT;

		var segmentWidth = max(1, round(0.8f * barWidth));
		var segmentHeight = max(1, round(0.7f * rowHeight));

		var left = MARGIN + 0.2f * barWidth;
		var bottom = height - MARGIN - rowHeight;

		// draw background

		graphics2D.setColor(BACKGROUND_COLOR);
		graphics2D.fillRect(0, 0, width, height);

		// draw segments

		for (var barIndex = 0; barIndex < barCount; barIndex++) {

			var magnitude = averages[barIndex];
			var decibels = (float) (20 * log10(magnitude / BASE_MAGNITUDE));
			var segmentCount = round(NORMALIZER.normalize(decibels));
			var segmentX = round(fma(barIndex, barWidth, left));

			for (var segmentIndex = 0; segmentIndex < segmentCount; segmentIndex++) {

				graphics2D.setColor(SEGMENT_COLOR[segmentIndex]);

				graphics2D.fillRect(
						segmentX,
						round(fma(-segmentIndex, rowHeight, bottom)),
						segmentWidth, segmentHeight);
			}

			// draw and update peak

			var peak = peaks[barIndex];

			if (decibels > peak) {

				// update peak

				peaks[barIndex] = decibels;
				peaksTime[barIndex] = 0.0;
				peaksSpeed[barIndex] = 0.0;

			} else {

				// update peak time

				var peakTime = peaksTime[barIndex];
				peakTime += targetFrameTime;
				peaksTime[barIndex] = peakTime;

				// after hanging time, let the peak fall

				if (peakTime > PEAKS_HANGING_TIME) {

					// increase peak falling speed with gravity

					var peakSpeed = peaksSpeed[barIndex];
					peakSpeed += PEAKS_GRAVITY * targetFrameTime;
					peaksSpeed[barIndex] = peakSpeed;

					// update peak

					peak -= peakSpeed * targetFrameTime;
					peaks[barIndex] = peak;
				}

				// draw peak

				var peakSegmentIndex = round(NORMALIZER.normalize(peak)) - 1;

				if (peakSegmentIndex > 0) {

					graphics2D.setColor(SEGMENT_COLOR[peakSegmentIndex]);

					graphics2D.fillRect(
							segmentX,
							round(fma(-peakSegmentIndex, rowHeight, bottom)),
							segmentWidth, segmentHeight);
				}
			}
		}

		drawFrameRate(graphics2D);
	}
}
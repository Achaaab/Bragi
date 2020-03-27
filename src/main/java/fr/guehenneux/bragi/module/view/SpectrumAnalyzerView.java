package fr.guehenneux.bragi.module.view;

import fr.guehenneux.bragi.Normalizer;
import fr.guehenneux.bragi.PainterThread;
import fr.guehenneux.bragi.module.model.SpectrumAnalyzer;

import javax.swing.JComponent;
import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;

import static java.awt.Color.BLACK;
import static java.lang.Math.log10;
import static java.lang.Math.round;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

/**
 * @author Jonathan Guéhenneux
 * @since 0.0.5
 */
public class SpectrumAnalyzerView extends JComponent {

	private static final int PLOT_MARGIN = 5;
	private static final int SEGMENT_COUNT = 100;
	private static final float BASE_MAGNITUDE = 10_000.0f;
	private static final float MINIMAL_DECIBELS = -60.0f;
	private static final float MAXIMAL_DECIBELS = 0.0f;

	private static final Normalizer NORMALIZER = new Normalizer(
			MINIMAL_DECIBELS, MAXIMAL_DECIBELS,
			0, SEGMENT_COUNT
	);

	private static final float[] GRADIENT_FRACTIONS = {0.0f, 0.5f, 1.0f};

	private static final Color[] GRADIENT_COLORS = {
			new Color(0, 0, 0),
			new Color(255, 128, 0),
			new Color(255, 128, 0)
	};

	private SpectrumAnalyzer model;

	/**
	 * @param model spectrum analyzer model
	 */
	public SpectrumAnalyzerView(SpectrumAnalyzer model) {

		this.model = model;

		var frame = new JFrame(model.getName());
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.setSize(640, 360);
		frame.setLayout(new BorderLayout());
		frame.add(this);
		frame.setVisible(true);

		new PainterThread(this, 60).start();
	}

	@Override
	public void paint(Graphics graphics) {

		var width = getWidth();
		var height = getHeight();

		var plotWidth = width - 2 * PLOT_MARGIN;
		var plotHeight = height - 2 * PLOT_MARGIN;

		var graphics2D = (Graphics2D) graphics;
		graphics2D.setColor(BLACK);
		graphics2D.fillRect(0, 0, width, height);

		var averages = model.getAverages();

		var barCount = averages.length;
		var barWidth = 0.8f * plotWidth / barCount;
		var barLeftMargin = 0.1f * plotWidth / barCount;
		var barHeight = 0.7f * plotHeight / SEGMENT_COUNT;
		var barTopMargin = 0.15f * plotHeight / SEGMENT_COUNT;
		var plotBottom = PLOT_MARGIN + plotHeight;

		var barPaint = new LinearGradientPaint(
				0, plotBottom,
				0, PLOT_MARGIN,
				GRADIENT_FRACTIONS, GRADIENT_COLORS);

		graphics2D.setPaint(barPaint);

		for (var barIndex = 0; barIndex < barCount; barIndex++) {

			var barX = PLOT_MARGIN + barIndex * plotWidth / barCount + barLeftMargin;
			var magnitude = averages[barIndex];
			var decibels = (float) (20 * log10(magnitude / BASE_MAGNITUDE));
			var segmentsOn = NORMALIZER.normalize(decibels);

			for (var segment = 0; segment < segmentsOn; segment++) {

				var barY = plotBottom - (segment + 1) * plotHeight / SEGMENT_COUNT + barTopMargin;
				graphics2D.fillRect(round(barX), round(barY), round(barWidth), round(barHeight));
			}
		}
	}
}
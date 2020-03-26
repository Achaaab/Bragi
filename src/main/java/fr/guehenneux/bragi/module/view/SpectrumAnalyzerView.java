package fr.guehenneux.bragi.module.view;

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
import static java.lang.Math.min;
import static java.lang.Math.round;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

/**
 * @author Jonathan Guéhenneux
 * @since 0.0.5
 */
public class SpectrumAnalyzerView extends JComponent {

	private static final int PLOT_MARGIN = 5;
	private static final int LEVEL_COUNT = 80;

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
		var barHeight = 0.8f * plotHeight / LEVEL_COUNT;
		var barTopMargin = 0.1f * plotHeight / LEVEL_COUNT;
		var plotBottom = PLOT_MARGIN + plotHeight;

		var barPaint = new LinearGradientPaint(
				PLOT_MARGIN, plotBottom,
				PLOT_MARGIN, PLOT_MARGIN,
				new float[]{0.0f, 0.5f, 1.0f},
				new Color[]{BLACK, new Color(255, 128, 0), new Color(255, 128, 0)});

		graphics2D.setPaint(barPaint);

		for (var barIndex = 0; barIndex < barCount; barIndex++) {

			var barX = PLOT_MARGIN + barIndex * plotWidth / barCount + barLeftMargin;
			var magnitude = averages[barIndex];

			var barLevel = round(20 * log10(magnitude));

			for (var levelIndex = 0; levelIndex <= min(barLevel, LEVEL_COUNT - 1); levelIndex++) {

				var barY = plotBottom - (levelIndex + 1) * plotHeight / LEVEL_COUNT + barTopMargin;
				graphics2D.fillRect(round(barX), round(barY), round(barWidth), round(barHeight));
			}
		}
	}
}
package fr.guehenneux.bragi.module.view;

import fr.guehenneux.bragi.ColorUtils;
import fr.guehenneux.bragi.PainterThread;
import fr.guehenneux.bragi.module.model.SpectrumAnalyzer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.*;

/**
 * @author Jonathan Guéhenneux
 */
public class SpectrumAnalyzerView extends JComponent {

	private static final int PLOT_MARGIN = 5;
	private static final int LEVEL_COUNT = 50;
	private static final Color COLOR_MAXIMUM = new Color(87, 40, 255);
	private static final Color COLOR_MINIMUM = new Color(255, 255, 255);

	private float[] averages;

	private SpectrumAnalyzer model;

	/**
	 * @param model
	 */
	public SpectrumAnalyzerView(SpectrumAnalyzer model) {

		this.model = model;

		JFrame frame = new JFrame(model.getName());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 300);
		frame.setLayout(new BorderLayout());
		frame.add(this, BorderLayout.CENTER);
		frame.setVisible(true);
		PainterThread painter = new PainterThread(this, 60);
		painter.start();
	}

	/**
	 * @param averages
	 */
	public synchronized void display(float[] averages) {
		this.averages = averages;
	}

	@Override
	public synchronized void paint(Graphics graphics) {

		int width = getWidth();
		int height = getHeight();

		float plotWidth = width - 2 * PLOT_MARGIN;
		float plotHeight = height - 2 * PLOT_MARGIN;

		Graphics2D graphics2D = (Graphics2D) graphics;
		graphics2D.setColor(Color.BLACK);
		graphics2D.fillRect(0, 0, width, height);

		if (averages != null) {

			int barCount = averages.length;

			float barWidth = 0.8f * plotWidth / barCount;
			float barLeftMargin = 0.1f * plotWidth / barCount;
			float barHeight = 0.8f * plotHeight / LEVEL_COUNT;
			float barTopMargin = 0.1f * plotHeight / LEVEL_COUNT;

			float barX;
			float barY;

			float plotBottom = PLOT_MARGIN + plotHeight;
			int barLevel;
			float magnitude;

			Color barColor;

			for (int barIndex = 0; barIndex < barCount; barIndex++) {

				barX = PLOT_MARGIN + barIndex * plotWidth / barCount + barLeftMargin;
				magnitude = averages[barIndex];
				barLevel = Math.round(magnitude * LEVEL_COUNT / 200);

				for (int levelIndex = 0; levelIndex <= Math.min(barLevel, LEVEL_COUNT - 1); levelIndex++) {

					barY = plotBottom - (levelIndex + 1) * plotHeight / LEVEL_COUNT + barTopMargin;
					barColor = ColorUtils.getColorValue(0, COLOR_MINIMUM, LEVEL_COUNT, COLOR_MAXIMUM, levelIndex);
					graphics2D.setColor(barColor);
					graphics2D.fillRect(Math.round(barX), Math.round(barY), Math.round(barWidth), Math.round(barHeight));
				}

				for (int levelIndex = Math.max(0, barLevel); levelIndex < LEVEL_COUNT; levelIndex++) {

					barY = plotBottom - (levelIndex + 1) * plotHeight / LEVEL_COUNT + barTopMargin;
					barColor = ColorUtils.getColorValue(0, COLOR_MINIMUM, LEVEL_COUNT, COLOR_MAXIMUM, levelIndex);
					graphics2D.setColor(barColor.darker().darker());
					graphics2D.fillRect(Math.round(barX), Math.round(barY), Math.round(barWidth), Math.round(barHeight));
				}
			}
		}
	}
}
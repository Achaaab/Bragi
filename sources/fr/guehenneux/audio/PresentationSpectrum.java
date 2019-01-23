package fr.guehenneux.audio;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * @author Jonathan Guéhenneux
 */
public class PresentationSpectrum extends JPanel {

	private static final int PLOT_MARGIN = 5;
	private static final int LEVEL_COUNT = 50;
	private static final Color COLOR_MAXIMUM = new Color(87, 40, 255);
	private static final Color COLOR_MINIMUM = new Color(255, 255, 255);

	private BufferedImage bufferImage;
	private Graphics2D bufferGraphics;
	private float[] averages;
	private Spectrum control;

	/**
	 * @param control
	 */
	public PresentationSpectrum(Spectrum control) {

		this.control = control;

		JFrame frame = new JFrame(control.getName());
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
	public void display(float[] averages) {
		this.averages = averages;
	}

	@Override
	public void paint(Graphics graphics) {

		int containerWidth = getWidth();
		int containerHeight = getHeight();

		int plotWidth = containerWidth - 2 * PLOT_MARGIN;
		int plotHeight = containerHeight - 2 * PLOT_MARGIN;

		if (bufferGraphics == null || containerWidth != bufferImage.getWidth() || containerHeight != bufferImage.getHeight()) {

			bufferImage = new BufferedImage(containerWidth, containerHeight, BufferedImage.TYPE_INT_RGB);
			bufferGraphics = bufferImage.createGraphics();
		}

		bufferGraphics.setColor(Color.BLACK);
		bufferGraphics.fillRect(0, 0, containerWidth, containerHeight);

		if (averages != null) {

			int barCount = averages.length;

			float barWidth = 0.8f * plotWidth / barCount;
			float barLeftMargin = 0.1f * plotWidth / barCount;
			float barHeight = 0.8f * plotHeight / LEVEL_COUNT;
			float barTopMargin = 0.1f * plotHeight / LEVEL_COUNT;

			float barX;
			float barY;

			int plotBottom = PLOT_MARGIN + plotHeight;
			int barLevel;
			float magnitude;

			Color barColor;

			for (int barIndex = 0; barIndex < barCount; barIndex++) {

				barX = PLOT_MARGIN + barIndex * plotWidth / barCount + barLeftMargin;
				magnitude = averages[barIndex];
				barLevel = (int) Math.round(magnitude * LEVEL_COUNT / 200);

				for (int levelIndex = 0; levelIndex <= Math.min(barLevel, LEVEL_COUNT - 1); levelIndex++) {

					barY = plotBottom - (levelIndex + 1) * plotHeight / LEVEL_COUNT + barTopMargin;
					barColor = ColorUtils.getColorValue(0, COLOR_MINIMUM, LEVEL_COUNT, COLOR_MAXIMUM, levelIndex);
					bufferGraphics.setColor(barColor);
					bufferGraphics.fillRect(Math.round(barX), Math.round(barY), Math.round(barWidth), Math.round(barHeight));
				}

				for (int levelIndex = Math.max(0, barLevel); levelIndex < LEVEL_COUNT; levelIndex++) {

					barY = plotBottom - (levelIndex + 1) * plotHeight / LEVEL_COUNT + barTopMargin;
					barColor = ColorUtils.getColorValue(0, COLOR_MINIMUM, LEVEL_COUNT, COLOR_MAXIMUM, levelIndex);
					bufferGraphics.setColor(barColor.darker().darker());
					bufferGraphics.fillRect(Math.round(barX), Math.round(barY), Math.round(barWidth), Math.round(barHeight));
				}
			}
		}

		graphics.drawImage(bufferImage, 0, 0, null);
	}

	/**
	 * @return the control
	 */
	public Spectrum getControl() {
		return control;
	}
}
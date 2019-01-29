package fr.guehenneux.bragi.module;

import fr.guehenneux.bragi.PainterThread;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 * @author Jonathan Guéhenneux
 */
public class PresentationOscilloscope extends JComponent {

	private static final int DEFAULT_PRECISION = 1600;
	private static final int MARGIN = 5;

	private float[] samples;
	private int precision;

	private Oscilloscope control;

	/**
	 * @param control
	 */
	public PresentationOscilloscope(Oscilloscope control) {

		this.control = control;

		precision = DEFAULT_PRECISION;

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
	 * @param samples samples to display
	 */
	public synchronized void display(float[] samples) {
		this.samples = samples;
	}

	@Override
	public synchronized void paint(Graphics graphics) {

		if (samples != null) {

			int width = getWidth();
			int height = getHeight();
			int plotWidth = width - 2 * MARGIN;
			int plotHeight = height - 2 * MARGIN;

			int sampleCount = samples.length;
			int displaySampleCount = Math.min(sampleCount, precision);
			int[] xValues = new int[displaySampleCount];
			int[] yValues = new int[displaySampleCount];

			int sampleIndex;
			float sample;

			for (int displaySampleIndex = 0; displaySampleIndex < displaySampleCount; displaySampleIndex++) {

				sampleIndex = displaySampleIndex * sampleCount / displaySampleCount;
				sample = samples[sampleIndex];
				xValues[displaySampleIndex] = MARGIN + plotWidth * sampleIndex / sampleCount;
				yValues[displaySampleIndex] = MARGIN + plotHeight / 2 - Math.round(plotHeight * sample / 2);
			}

			Graphics2D graphics2D = (Graphics2D)graphics;
			graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			graphics2D.setStroke(new BasicStroke(2.0f));

			graphics2D.setColor(Color.BLACK);
			graphics2D.fillRect(0, 0, width, height);

			if (xValues != null) {

				graphics2D.setColor(new Color(64, 224, 224));
				graphics2D.drawPolyline(xValues, yValues, xValues.length);
			}
		}
	}
}
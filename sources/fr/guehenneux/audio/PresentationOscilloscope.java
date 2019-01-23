package fr.guehenneux.audio;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.*;

/**
 * @author Jonathan Guéhenneux
 */
public class PresentationOscilloscope extends JComponent {

	private static final int DEFAULT_PRECISION = 1600;
	private static final int MARGIN = 5;

	private BufferedImage bufferImage;
	private Graphics2D bufferGraphics;
	private int precision;
	private int[] xValues;
	private int[] yValues;

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

	public synchronized void afficher(float[] samples) {

		int containerWidth = getWidth();
		int containerHeight = getHeight();

		int plotWidth = containerWidth - 2 * MARGIN;
		int plotHeight = containerHeight - 2 * MARGIN;

		int sampleCount = samples.length;

		int displaySampleCount = Math.min(sampleCount, precision);

		xValues = new int[displaySampleCount];
		yValues = new int[displaySampleCount];

		int sampleIndex;
		float sample;

		for (int displaySampleIndex = 0; displaySampleIndex < displaySampleCount; displaySampleIndex++) {

			sampleIndex = displaySampleIndex * sampleCount / displaySampleCount;
			sample = samples[sampleIndex];
			xValues[displaySampleIndex] = MARGIN + plotWidth * sampleIndex / sampleCount;
			yValues[displaySampleIndex] = MARGIN + plotHeight / 2 - Math.round(plotHeight * sample / 2);
		}
	}

	public synchronized void paint(Graphics graphics) {

		int containerWidth = getWidth();
		int containerHeight = getHeight();

		if (bufferGraphics == null || containerWidth != bufferImage.getWidth() || containerHeight != bufferImage.getHeight()) {

			bufferImage = new BufferedImage(containerWidth, containerHeight, BufferedImage.TYPE_INT_RGB);
			bufferGraphics = bufferImage.createGraphics();
			bufferGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			bufferGraphics.setStroke(new BasicStroke(3.0f));
		}

		bufferGraphics.setColor(Color.BLACK);
		bufferGraphics.fillRect(0, 0, containerWidth, containerHeight);

		if (xValues != null) {

			bufferGraphics.setColor(new Color(64, 224, 224));
			bufferGraphics.drawPolyline(xValues, yValues, xValues.length);
		}

		graphics.drawImage(bufferImage, 0, 0, null);
	}

	/**
	 * @return the control
	 */
	public Oscilloscope getControl() {
		return control;
	}
}
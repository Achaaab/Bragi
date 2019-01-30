package fr.guehenneux.bragi.module.view;

import fr.guehenneux.bragi.PainterThread;
import fr.guehenneux.bragi.module.model.Oscilloscope;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 * @author Jonathan Guéhenneux
 */
public class OscilloscopeView extends JComponent {

	private static final int DEFAULT_PRECISION = 1600;
	private static final int MARGIN = 5;

	private float[] samples;
	private int precision;

	private Oscilloscope model;

	/**
	 * @param model
	 */
	public OscilloscopeView(Oscilloscope model) {

		this.model = model;

		precision = DEFAULT_PRECISION;

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
	 * @param samples samples to display
	 */
	public synchronized void display(float[] samples) {
		this.samples = samples;
	}

	@Override
	public synchronized void paint(Graphics graphics) {

		int width = getWidth();
		int height = getHeight();

		Graphics2D graphics2D = (Graphics2D)graphics;
		graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics2D.setStroke(new BasicStroke(2.0f));

		graphics2D.setColor(Color.BLACK);
		graphics2D.fillRect(0, 0, width, height);

		if (samples != null) {

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
				sample = Math.max(-1.0f, Math.min(1.0f, sample));

				xValues[displaySampleIndex] = MARGIN + plotWidth * sampleIndex / sampleCount;
				yValues[displaySampleIndex] = MARGIN + plotHeight / 2 - Math.round(plotHeight * sample / 2);
			}

			graphics2D.setColor(new Color(64, 224, 224));
			graphics2D.drawPolyline(xValues, yValues, xValues.length);
		}
	}
}
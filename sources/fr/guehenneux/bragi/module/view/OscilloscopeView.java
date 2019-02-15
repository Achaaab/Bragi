package fr.guehenneux.bragi.module.view;

import fr.guehenneux.bragi.PainterThread;
import fr.guehenneux.bragi.ShiftingFloatArray;
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

	private static final int MARGIN = 5;
	private static final int HORIZONTAL_DIVISION_COUNT = 10;

	private Oscilloscope model;

	/**
	 * @param model
	 */
	public OscilloscopeView(Oscilloscope model) {

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

	@Override
	public void paint(Graphics graphics) {

		int width = getWidth();
		int height = getHeight();
		int plotWidth = width - 2 * MARGIN;
		int plotHeight = height - 2 * MARGIN;

		Graphics2D graphics2D = (Graphics2D)graphics;
		graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		graphics2D.setColor(Color.BLACK);
		graphics2D.fillRect(0, 0, width, height);

		graphics2D.setStroke(new BasicStroke(1.0f));
		graphics2D.setPaint(Color.LIGHT_GRAY);

		for (int horizontalDivisionIndex = 0; horizontalDivisionIndex <= HORIZONTAL_DIVISION_COUNT; horizontalDivisionIndex++) {

			graphics2D.drawLine(MARGIN + horizontalDivisionIndex * plotWidth / HORIZONTAL_DIVISION_COUNT, MARGIN,
					MARGIN + horizontalDivisionIndex * plotWidth / HORIZONTAL_DIVISION_COUNT, MARGIN + plotHeight);
		}

		int verticalDivision = 0;

		while (verticalDivision <= plotHeight / 2) {

			graphics2D.drawLine(MARGIN, MARGIN + plotHeight / 2 + verticalDivision,
					MARGIN + plotWidth, MARGIN + plotHeight / 2 + verticalDivision);

			graphics2D.drawLine(MARGIN, MARGIN + plotHeight / 2 - verticalDivision,
					MARGIN + plotWidth, MARGIN + plotHeight / 2 - verticalDivision);

			verticalDivision += plotWidth / HORIZONTAL_DIVISION_COUNT;
		}

		graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics2D.setStroke(new BasicStroke(2.0f));

		ShiftingFloatArray buffer = model.getBuffer();
		int sampleCount = buffer.getLength();
		float sample;
		int[] xValues = new int[sampleCount];
		int[] yValues = new int[sampleCount];

		synchronized (buffer) {

			for (int sampleIndex = 0; sampleIndex < sampleCount; sampleIndex++) {

				sample = buffer.read(sampleIndex);
				sample = Math.max(-1.0f, Math.min(1.0f, sample));

				xValues[sampleIndex] = MARGIN + plotWidth * sampleIndex / sampleCount;
				yValues[sampleIndex] = MARGIN + plotHeight / 2 - Math.round(plotHeight * sample / 2);
			}
		}

		graphics2D.setColor(new Color(64, 224, 224));
		graphics2D.drawPolyline(xValues, yValues, xValues.length);
	}
}
package fr.guehenneux.bragi.module.view;

import fr.guehenneux.bragi.PainterThread;
import fr.guehenneux.bragi.Settings;
import fr.guehenneux.bragi.module.model.Oscilloscope;

import javax.swing.JComponent;
import javax.swing.JFrame;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;

import static java.awt.BorderLayout.CENTER;
import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_OFF;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;
import static java.lang.Math.round;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

/**
 * oscilloscope view
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.4
 */
public class OscilloscopeView extends JComponent {

	private static final int MARGIN = 5;
	private static final int HORIZONTAL_DIVISION_COUNT = 10;
	private static final Color PLOT_COLOR = new Color(64, 224, 224);
	private static final Color BACKGROUND_COLOR = new Color(0, 0, 0);
	private static final Color DIVISION_COLOR = new Color(192, 192, 192);
	private static final Stroke PLOT_STROKE = new BasicStroke(2.0f);
	private static final Stroke DIVISION_STROKE = new BasicStroke(1.0f);
	private static final float SECONDS_PER_DIVISION = 0.0017f;
	private static final float VOLTS_PER_DIVISION = 1.0f;

	private Oscilloscope model;

	/**
	 * @param model model
	 */
	public OscilloscopeView(Oscilloscope model) {

		this.model = model;

		var frame = new JFrame(model.getName());
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.setSize(400, 300);
		frame.setLayout(new BorderLayout());
		frame.add(this, CENTER);
		frame.setVisible(true);

		var painter = new PainterThread(this, 60);
		painter.start();
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

		var buffer = model.getBuffer();
		var sampleCount = buffer.getLength();
		var xValues = new int[sampleCount];
		var yValues = new int[sampleCount];

		synchronized (buffer) {

			for (var sampleIndex = 0; sampleIndex < sampleCount; sampleIndex++) {

				var sample = buffer.read(sampleIndex);
				var time = (float) sampleIndex / Settings.INSTANCE.getFrameRate();

				xValues[sampleIndex] = round(MARGIN + divisionSize * time / SECONDS_PER_DIVISION);
				yValues[sampleIndex] = round(MARGIN + plotHeight / 2 - divisionSize * sample / VOLTS_PER_DIVISION);
			}
		}

		graphics2D.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
		graphics2D.setStroke(PLOT_STROKE);
		graphics2D.setColor(PLOT_COLOR);
		graphics2D.drawPolyline(xValues, yValues, xValues.length);
	}
}
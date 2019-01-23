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
 * @author GUEHENNEUX
 */
public class PresentationSpectrum extends JPanel {

	/**
     * 
     */
	private static final long serialVersionUID = 81458634838225184L;

	private static final int PLOT_MARGIN = 5;

	private static final float MAXIMUM_LEVEL = 20.0f;

	private static final int LEVEL_COUNT = 20;

	private static final double FREQUENCY_BAND_POW = 0.1;

	private static final Color COLOR_MAXIMUM = new Color(87, 40, 255);

	private static final Color COLOR_MINIMUM = new Color(255, 255, 255);

	private static final List<FrequencyBand> FREQUENCY_BANDS;

	static {

		FREQUENCY_BANDS = new ArrayList<FrequencyBand>();

		double rf = Math.pow(2.0, FREQUENCY_BAND_POW);

		double f1 = 0.0;
		double f2 = 50.0;

		FrequencyBand frequencyBand;

		while (f2 < FormatAudio.getInstance().getFrameRate() / 2) {

			frequencyBand = new FrequencyBand((float) f1, (float) f2);
			FREQUENCY_BANDS.add(frequencyBand);
			f1 = f2;
			f2 *= rf;
		}
	}

	private BufferedImage bufferImage;

	private Graphics2D bufferGraphics;

	private Map<FrequencyBand, FrequencyBandLevel> spectrum;

	private Spectrum controle;

	/**
     * 
     *
     */
	public PresentationSpectrum(Spectrum controle) {

		this.controle = controle;

		spectrum = new TreeMap<FrequencyBand, FrequencyBandLevel>();
		clearSpectrum();

		JFrame fenetre = new JFrame(controle.getName());
		fenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		fenetre.setSize(400, 300);
		fenetre.setLayout(new BorderLayout());
		fenetre.add(this, BorderLayout.CENTER);
		fenetre.setVisible(true);
		PainterThread painter = new PainterThread(this, 60);
		painter.start();
	}

	private void clearSpectrum() {

		for (FrequencyBand frequencyBand : FREQUENCY_BANDS) {
			spectrum.put(frequencyBand, new FrequencyBandLevel());
		}
	}

	/**
	 * @param spectrumData
	 */
	public void display(float[] spectrumData) {

		clearSpectrum();

		int binCount = spectrumData.length;
		double resolution = FormatAudio.getInstance().getFrameRate() / 2.0 / binCount;

		double real;
		double imaginary;
		double magnitude;
		float frequency;

		FrequencyBand frequencyBand;
		FrequencyBandLevel frequencyBandLevel;

		for (int binIndex = 0; binIndex < binCount; binIndex++) {

			magnitude = spectrumData[binIndex];
			frequency = (float) (resolution * binIndex);

			frequencyBand = new FrequencyBand(frequency);

			frequencyBandLevel = spectrum.get(frequencyBand);

			if (frequencyBandLevel != null) {
				frequencyBandLevel.add(Math.sqrt(magnitude));
			}
		}
	}

	public void paint(Graphics graphics) {

		int containerWidth = getWidth();
		int containerHeight = getHeight();

		int plotWidth = containerWidth - 2 * PLOT_MARGIN;
		int plotHeight = containerHeight - 2 * PLOT_MARGIN;

		if (bufferGraphics == null || containerWidth != bufferImage.getWidth()
				|| containerHeight != bufferImage.getHeight()) {

			bufferImage = new BufferedImage(containerWidth, containerHeight, BufferedImage.TYPE_INT_RGB);

			bufferGraphics = bufferImage.createGraphics();
		}

		bufferGraphics.setColor(Color.BLACK);
		bufferGraphics.fillRect(0, 0, containerWidth, containerHeight);

		int barCount = FREQUENCY_BANDS.size();

		float barWidth = 0.8f * plotWidth / barCount;
		float barLeftMargin = 0.1f * plotWidth / barCount;
		float barHeight = 0.8f * plotHeight / LEVEL_COUNT;
		float barTopMargin = 0.1f * plotHeight / LEVEL_COUNT;

		float barX;
		float barY;

		int plotBottom = PLOT_MARGIN + plotHeight;
		int barLevel;
		double level;
		FrequencyBand frequencyBand;
		FrequencyBandLevel frequencyBandLevel;

		Color barColor;

		for (int barIndex = 0; barIndex < barCount; barIndex++) {

			barX = PLOT_MARGIN + barIndex * plotWidth / barCount + barLeftMargin;

			frequencyBand = FREQUENCY_BANDS.get(barIndex);
			frequencyBandLevel = spectrum.get(frequencyBand);

			level = frequencyBandLevel.getLevel();

			barLevel = (int) Math.round(level * LEVEL_COUNT / MAXIMUM_LEVEL);

			for (int levelIndex = 0; levelIndex <= Math.min(barLevel, LEVEL_COUNT - 1); levelIndex++) {

				barY = plotBottom - (levelIndex + 1) * plotHeight / LEVEL_COUNT + barTopMargin;

				barColor = ColorUtils.getColorValue(0, COLOR_MINIMUM, LEVEL_COUNT, COLOR_MAXIMUM, levelIndex);
				bufferGraphics.setColor(barColor);
				bufferGraphics
						.fillRect(Math.round(barX), Math.round(barY), Math.round(barWidth), Math.round(barHeight));
			}

			for (int levelIndex = Math.max(0, barLevel); levelIndex < LEVEL_COUNT; levelIndex++) {

				barY = plotBottom - (levelIndex + 1) * plotHeight / LEVEL_COUNT + barTopMargin;
				barColor = ColorUtils.getColorValue(0, COLOR_MINIMUM, LEVEL_COUNT, COLOR_MAXIMUM, levelIndex);
				bufferGraphics.setColor(barColor.darker().darker());
				bufferGraphics
						.fillRect(Math.round(barX), Math.round(barY), Math.round(barWidth), Math.round(barHeight));

			}
		}

		graphics.drawImage(bufferImage, 0, 0, null);
	}

	/**
	 * @return the controle
	 */
	public Spectrum getControle() {
		return controle;
	}
}
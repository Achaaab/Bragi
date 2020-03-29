package fr.guehenneux.bragi.module;

import fr.guehenneux.bragi.common.CircularFloatArray;
import fr.guehenneux.bragi.common.Settings;
import fr.guehenneux.bragi.common.connection.Input;
import fr.guehenneux.bragi.common.fft.FFT;
import fr.guehenneux.bragi.common.fft.HammingWindow;
import fr.guehenneux.bragi.gui.module.SpectrumAnalyzerView;

/**
 * @author Jonathan Guéhenneux
 * @since 0.0.5
 */
public class SpectrumAnalyzer extends Module {

	private static final int FFT_SAMPLE_COUNT = 1 << 12;

	private Input input;

	private FFT fft;

	private float[] fftSamples;
	private final CircularFloatArray buffer;

	/**
	 * @param name name of the spectrum analyzer to create
	 */
	public SpectrumAnalyzer(String name) {

		super(name);

		input = addPrimaryInput(name + "_input");

		fft = new FFT(FFT_SAMPLE_COUNT, Settings.INSTANCE.getFrameRate());
		fft.setWindow(new HammingWindow());
		fft.logAverages(50, 12);

		fftSamples = new float[FFT_SAMPLE_COUNT];
		buffer = new CircularFloatArray(FFT_SAMPLE_COUNT);

		new SpectrumAnalyzerView(this);

		start();
	}

	@Override
	public int compute() throws InterruptedException {

		var samples = input.read();

		synchronized (buffer) {
			buffer.write(samples);
		}

		return samples.length;
	}

	/**
	 * @return spectrum averages
	 */
	public float[] getAverages() {

		synchronized (buffer) {
			buffer.readLast(fftSamples);
		}

		fft.forward(fftSamples);

		return fft.getAverages();
	}
}
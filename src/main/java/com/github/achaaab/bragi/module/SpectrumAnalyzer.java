package com.github.achaaab.bragi.module;

import com.github.achaaab.bragi.common.CircularFloatArray;
import com.github.achaaab.bragi.common.ModuleCreationException;
import com.github.achaaab.bragi.common.Settings;
import com.github.achaaab.bragi.common.fft.FFT;
import com.github.achaaab.bragi.common.fft.HammingWindow;
import com.github.achaaab.bragi.gui.module.SpectrumAnalyzerView;
import com.github.achaaab.bragi.common.connection.Input;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * A spectrum analyzer converts a time domain signal to a frequency domain signal.
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.5
 */
public class SpectrumAnalyzer extends Module {

	private static final Logger LOGGER = getLogger(SpectrumAnalyzer.class);

	public static final String DEFAULT_NAME = "spectrum_analyzer";

	private static final int FFT_SAMPLE_COUNT = 1 << 12;

	private Input input;

	private FFT fft;

	private float[] fftSamples;
	private final CircularFloatArray buffer;

	/**
	 * Creates a spectrum analyzer with default name.
	 *
	 * @see #DEFAULT_NAME
	 * @since 0.0.9
	 */
	public SpectrumAnalyzer() {
		this(DEFAULT_NAME);
	}

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
package com.github.achaaab.bragi.core.module.consumer;

import com.github.achaaab.bragi.common.CircularFloatArray;
import com.github.achaaab.bragi.common.Settings;
import com.github.achaaab.bragi.core.connection.Input;
import com.github.achaaab.bragi.dsp.fft.FastFourierTransform;
import com.github.achaaab.bragi.dsp.fft.FourierTransform;
import com.github.achaaab.bragi.dsp.fft.HammingWindow;
import com.github.achaaab.bragi.gui.module.SpectrumAnalyzerView;
import com.github.achaaab.bragi.core.module.Module;
import org.slf4j.Logger;

import static javax.swing.SwingUtilities.invokeLater;
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

	private static final int FOURIER_TRANSFORM_SIZE = 1 << 15;

	private final Input input;

	private final FourierTransform fourierTransform;
	private final float[] fourierTransformSamples;

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

		fourierTransform = new FastFourierTransform(FOURIER_TRANSFORM_SIZE, Settings.INSTANCE.frameRate());
		fourierTransform.setWindow(new HammingWindow());
		fourierTransform.logarithmicAverages(13.75, 12);

		fourierTransformSamples = new float[FOURIER_TRANSFORM_SIZE];
		buffer = new CircularFloatArray(FOURIER_TRANSFORM_SIZE);

		invokeLater(() -> new SpectrumAnalyzerView(this));

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
			buffer.readLast(fourierTransformSamples);
		}

		fourierTransform.forward(fourierTransformSamples);

		return fourierTransform.getAverages();
	}
}
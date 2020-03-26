package fr.guehenneux.bragi.module.model;

import fr.guehenneux.bragi.Settings;
import fr.guehenneux.bragi.fft.FFT;
import fr.guehenneux.bragi.fft.HammingWindow;
import fr.guehenneux.bragi.connection.Input;
import fr.guehenneux.bragi.module.view.SpectrumAnalyzerView;

import static java.lang.Math.max;
import static java.lang.System.arraycopy;
import static java.util.Arrays.copyOf;

/**
 * @author Jonathan Guéhenneux
 * @since 0.0.5
 */
public class SpectrumAnalyzer extends Module {

	private static final int FFT_SAMPLE_COUNT = 1 << 13;

	private Input input;

	private FFT fft;
	private final float[] buffer;
	private int bufferIndex;

	private SpectrumAnalyzerView view;

	/**
	 * @param name name of the spectrum analyzer to create
	 */
	public SpectrumAnalyzer(String name) {

		super(name);

		input = addPrimaryInput(name + "_input");

		buffer = new float[FFT_SAMPLE_COUNT];
		bufferIndex = 0;

		fft = new FFT(FFT_SAMPLE_COUNT, Settings.INSTANCE.getFrameRate());
		fft.setWindow(new HammingWindow());
		fft.logAverages(50, 12);

		new SpectrumAnalyzerView(this);

		start();
	}

	@Override
	public int compute() throws InterruptedException {

		var samples = input.read();
		copy(samples);
		return samples.length;
	}

	/**
	 * Copy samples to FFT samples.
	 *
	 * @param samples samples to copy
	 */
	private void copy(float[] samples) {

		var start = max(samples.length - buffer.length, 0);
		var end = samples.length;

		copy(samples, start, end);
	}

	/**
	 * Copy samples to FFT samples.
	 *
	 * @param samples samples to copy
	 * @param start   index of the first sample to copy (included)
	 * @param end     index of the last sample to copy (excluded)
	 */
	private void copy(float[] samples, int start, int end) {

		var spaceLeft = buffer.length - bufferIndex;
		var numberOfSamplesToWrite = end - start;

		synchronized (buffer) {

			if (spaceLeft < numberOfSamplesToWrite) {

				arraycopy(samples, start, buffer, bufferIndex, spaceLeft);
				bufferIndex = numberOfSamplesToWrite - spaceLeft;
				arraycopy(samples, spaceLeft, buffer, 0, bufferIndex);

			} else {

				arraycopy(samples, start, buffer, bufferIndex, numberOfSamplesToWrite);
				bufferIndex = (bufferIndex + numberOfSamplesToWrite) % buffer.length;
			}
		}
	}

	/**
	 * @return spectrum averages
	 */
	public float[] getAverages() {

		float[] fftSamples;

		synchronized (buffer) {
			fftSamples = copyOf(buffer, buffer.length);
		}

		fft.forward(fftSamples);
		return fft.getAverages();
	}
}
package fr.guehenneux.bragi.module.model;

import fr.guehenneux.bragi.algorithm.FFT;
import fr.guehenneux.bragi.algorithm.FourierTransform;
import fr.guehenneux.bragi.Settings;
import fr.guehenneux.bragi.connection.Input;
import fr.guehenneux.bragi.module.view.SpectrumAnalyzerView;

import static java.lang.Math.min;
import static java.lang.System.arraycopy;

/**
 * @author Jonathan Guéhenneux
 */
public class SpectrumAnalyzer extends Module {

	private static final int FFT_SAMPLE_COUNT = 1 << 10;

	private Input input;

	private FFT fft;
	private float[] fftSamples;
	private int fftSampleIndex;

	private SpectrumAnalyzerView presentation;

	/**
	 * @param name
	 */
	public SpectrumAnalyzer(String name) {

		super(name);

		input = addInput(name + "_input");
		presentation = new SpectrumAnalyzerView(this);

		fftSamples = new float[FFT_SAMPLE_COUNT];
		fftSampleIndex = 0;

		fft = new FFT(FFT_SAMPLE_COUNT, Settings.INSTANCE.getFrameRate());
		fft.window(FourierTransform.HAMMING);
		fft.logAverages(50, 6);

		start();
	}

	@Override
	public int compute() throws InterruptedException {

		var samples = input.read();
		var sampleCount = samples.length;
		var sampleIndex = 0;

		while (sampleIndex < sampleCount) {

			var readSampleCount = min(sampleCount - sampleIndex, FFT_SAMPLE_COUNT - fftSampleIndex);
			arraycopy(samples, sampleIndex, fftSamples, fftSampleIndex, readSampleCount);

			sampleIndex += readSampleCount;
			fftSampleIndex += readSampleCount;

			if (fftSampleIndex == FFT_SAMPLE_COUNT) {

				fft.forward(fftSamples);
				presentation.display(fft.getAverages());
				fftSampleIndex = 0;
			}
		}

		return sampleCount;
	}
}
package fr.guehenneux.bragi.module;

import fr.guehenneux.bragi.FFT;
import fr.guehenneux.bragi.Settings;

/**
 * @author Jonathan Guéhenneux
 */
public class Spectrum extends Module {

	private static final int FFT_SAMPLE_COUNT = 1 << 10;

	private Input input;

	private FFT fft;
	private float[] fftSamples;
	private int fftSampleIndex;
	private int fftSampleCount;

	private PresentationSpectrum presentation;

	/**
	 * @param name
	 */
	public Spectrum(String name) {

		super(name);

		input = new Input();
		presentation = new PresentationSpectrum(this);

		fftSampleIndex = 0;
		fftSampleCount = 0;
		fftSamples = new float[FFT_SAMPLE_COUNT];

		fft = new FFT(FFT_SAMPLE_COUNT, Settings.INSTANCE.getFrameRate());
		//fft.window(FourierTransform.HAMMING);
		fft.logAverages(50, 6);
	}

	@Override
	public void compute() throws InterruptedException {

		if (input.isConnected()) {

			float[] samples = input.read();

			int sampleCount = Math.min(samples.length, FFT_SAMPLE_COUNT - fftSampleIndex);

			float sample;

			for (int sampleIndex = 0; sampleIndex < sampleCount; sampleIndex++) {

				sample = samples[sampleIndex];

				fftSamples[fftSampleIndex] = sample;
				fftSampleIndex++;
			}

			fftSampleCount += sampleCount;

			if (fftSampleCount == FFT_SAMPLE_COUNT) {

				fft.forward(fftSamples);
				float[] averages = fft.getAverages();

				presentation.display(averages);
				fftSampleIndex = 0;
				fftSampleCount = 0;
			}
		}
	}

	/**
	 * @return input
	 */
	public Input getInput() {
		return input;
	}
}
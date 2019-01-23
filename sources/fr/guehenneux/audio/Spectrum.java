package fr.guehenneux.audio;

import java.util.Arrays;

/**
 * @author GUEHENNEUX
 */
public class Spectrum extends Module {

	private static final int FFT_SAMPLE_COUNT = 1 << 10;

	private float[] fftSamples;

	private int fftSampleIndex;

	private int fftSampleCount;

	private InputPort portEntree;

	private PresentationSpectrum presentation;

	private FFT fft;

	/**
	 * 
	 * @param name
	 */
	public Spectrum(String name) {

		super(name);

		portEntree = new InputPort();
		presentation = new PresentationSpectrum(this);

		fftSampleIndex = 0;
		fftSampleCount = 0;
		fftSamples = new float[FFT_SAMPLE_COUNT];

		fft = new FFT(FFT_SAMPLE_COUNT, FormatAudio.getInstance().getFrameRate());
	}

	@Override
	public void compute() {

		float[] inputSamples = portEntree.read();

		int sampleCount = Math.min(inputSamples.length, FFT_SAMPLE_COUNT - fftSampleIndex);

		float sample;

		for (int sampleIndex = 0; sampleIndex < sampleCount; sampleIndex++) {

			sample = inputSamples[sampleIndex];

			fftSamples[fftSampleIndex] = sample;
			fftSampleIndex++;
		}

		fftSampleCount += sampleCount;

		if (fftSampleCount == FFT_SAMPLE_COUNT) {

			fft.forward(fftSamples);
			float[] spectrum = fft.getSpectrum();

			presentation.display(spectrum);
			fftSampleIndex = 0;
			fftSampleCount = 0;
		}
	}

	/**
	 * @return portEntree
	 */
	public InputPort getInputPort() {
		return portEntree;
	}
}
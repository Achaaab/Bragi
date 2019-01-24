package fr.guehenneux.audio;

/**
 * @author Jonathan Guéhenneux
 */
public class Spectrum extends Module {

	private static final int FFT_SAMPLE_COUNT = 1 << 10;

	private InputPort inputPort;

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

		inputPort = new InputPort();
		presentation = new PresentationSpectrum(this);

		fftSampleIndex = 0;
		fftSampleCount = 0;
		fftSamples = new float[FFT_SAMPLE_COUNT];

		fft = new FFT(FFT_SAMPLE_COUNT, Settings.INSTANCE.getFrameRate());
		fft.window(FourierTransform.HAMMING);
		fft.logAverages(50, 6);
	}

	@Override
	public void compute() throws InterruptedException {

		float[] samples = inputPort.read();

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

	/**
	 * @return inputPort
	 */
	public InputPort getInputPort() {
		return inputPort;
	}
}
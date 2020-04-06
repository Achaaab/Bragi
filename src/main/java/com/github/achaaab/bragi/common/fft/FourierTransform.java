package com.github.achaaab.bragi.common.fft;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.pow;
import static java.lang.Math.round;
import static java.lang.Math.sqrt;
import static java.lang.System.arraycopy;

/**
 * The Fourier Transform (FT) decomposes a signal into its constituent frequencies.
 *
 * @author Damien Di Fede
 * @author Jonathan Guéhenneux
 * @since 0.1.0
 */
public abstract class FourierTransform {

	protected static final int LINEAR_AVERAGE = 2;
	protected static final int LOGARITHMIC_AVERAGE = 3;
	protected static final int NO_AVERAGE = 4;

	protected final int size;
	protected final float sampleRate;
	protected final float bandWidth;

	protected float[] real;
	protected float[] imaginary;
	protected float[] spectrum;
	protected float[] averages;
	protected int whichAverage;
	protected int octaves;
	protected int avgPerOctave;

	protected Window window;

	/**
	 * Construct a FourierTransform that will analyze sample buffers that are {@code size} samples long
	 * and contain samples with a {@code sampleRate} sample rate.
	 *
	 * @param size       length of the buffers that will be analyzed
	 * @param sampleRate sample rate of the samples that will be analyzed
	 */
	FourierTransform(int size, float sampleRate) {

		this.size = size;
		this.sampleRate = sampleRate;

		bandWidth = sampleRate / size;

		noAverages();
		allocateArrays();

		window = NoWindow.INSTANCE;
	}

	/**
	 * Allocating real, imaginary, and spectrum are the responsibility of derived classes
	 * because the size of the arrays will depend on the implementation being used.
	 * This method enforces that responsibility.
	 */
	protected abstract void allocateArrays();

	/**
	 * Sets new complex values.
	 *
	 * @param real      new real part
	 * @param imaginary new imaginary part
	 */
	protected void setComplex(float[] real, float[] imaginary) {

		arraycopy(real, 0, this.real, 0, real.length);
		arraycopy(imaginary, 0, this.imaginary, 0, imaginary.length);
	}

	/**
	 * Fill the spectrum array with the amplitudes of the data in real and imaginary.
	 * Used so that this class can handle creating the average array and also do spectrum shaping if necessary.
	 */
	protected void fillSpectrum() {

		for (var spectrumIndex = 0; spectrumIndex < spectrum.length; spectrumIndex++) {

			var realPart = real[spectrumIndex];
			var imaginaryPart = imaginary[spectrumIndex];

			spectrum[spectrumIndex] = (float) sqrt(realPart * realPart + imaginaryPart * imaginaryPart);
		}

		if (whichAverage == LINEAR_AVERAGE) {

			var averageWidth = spectrum.length / averages.length;
			var spectrumIndex = 0;

			for (var averageIndex = 0; averageIndex < averages.length; averageIndex++) {

				var sum = 0.0f;
				var count = 0;

				while (count < averageWidth && spectrumIndex < spectrum.length) {

					sum += spectrum[spectrumIndex];

					spectrumIndex++;
					count++;
				}

				averages[averageIndex] = sum / count;
			}

		} else if (whichAverage == LOGARITHMIC_AVERAGE) {

			for (var octave = 0; octave < octaves; octave++) {

				float lowFrequency, hiFreq, freqStep;

				if (octave == 0) {
					lowFrequency = 0;
				} else {
					lowFrequency = (sampleRate / 2.0f) / (float) pow(2, octaves - octave);
				}

				hiFreq = (sampleRate / 2.0f) / (float) pow(2, octaves - octave - 1);

				freqStep = (hiFreq - lowFrequency) / avgPerOctave;

				var f = lowFrequency;

				for (var j = 0; j < avgPerOctave; j++) {

					var offset = j + octave * avgPerOctave;
					averages[offset] = getAverageAmplitude(f, f + freqStep);
					f += freqStep;
				}
			}
		}
	}

	/**
	 * Sets the object to not compute averages.
	 */
	public void noAverages() {
		whichAverage = NO_AVERAGE;
	}

	/**
	 * Sets the number of averages used when computing the spectrum and spaces the averages in a linear manner.
	 * In other words, each average band will be {@code specSize() / numAvg} bands wide.
	 *
	 * @param averageCount how many averages to compute
	 */
	public void linearAverage(int averageCount) {

		averages = new float[averageCount];
		whichAverage = LINEAR_AVERAGE;
	}

	/**
	 * Sets the number of averages used when computing the spectrum based on the minimum bandwidth
	 * for an octave and the number of bands per octave.
	 * <p>
	 * For example, with audio that has a sample rate of 44100 Hz,
	 * <code>logarithmicAverages(11, 1)</code> will result in 12 averages,
	 * each corresponding to an octave, the first spanning 0 to 11 Hz.
	 * <p>
	 * To ensure that each octave band is a full octave, the number of octaves is computed
	 * by dividing the Nyquist frequency by two, and then the result of that by two, and so on.
	 * <p>
	 * This means that the actual bandwidth of the lowest octave may not be exactly the value specified.
	 *
	 * @param minimumBandwidth minimum bandwidth used for an octave
	 * @param bandsPerOctave   how many bands to split each octave into
	 */
	public void logarithmicAverages(double minimumBandwidth, int bandsPerOctave) {

		var nyquistFrequency = sampleRate / 2;
		octaves = 1;

		while ((nyquistFrequency /= 2) > minimumBandwidth) {
			octaves++;
		}

		avgPerOctave = bandsPerOctave;
		averages = new float[octaves * bandsPerOctave];
		whichAverage = LOGARITHMIC_AVERAGE;
	}

	/**
	 * @param window window to use on the samples before taking the forward transform
	 */
	public void setWindow(Window window) {
		this.window = window;
	}

	/**
	 * Returns the length of the time domain signal expected by this transform.
	 *
	 * @return the length of the time domain signal expected by this transform
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Returns the size of the spectrum created by this transform. In other words, the number of frequency bands produced by this
	 * transform. This is typically equal to <code>timeSize()/2 + 1</code>, see above for an explanation.
	 *
	 * @return the size of the spectrum
	 */
	public int getSpectrumSize() {
		return spectrum.length;
	}

	/**
	 * Returns the amplitude of the requested frequency band.
	 *
	 * @param band index of a frequency band
	 * @return amplitude of the requested frequency band
	 */
	public float getAmplitude(int band) {

		band = max(0, min(spectrum.length - 1, band));
		return spectrum[band];
	}

	/**
	 * Returns the width of each frequency band in the spectrum (in Hz).
	 * It should be noted that the bandwidth of the first and
	 * last frequency bands is half as large as the value returned by this function.
	 *
	 * @return width of each frequency band in Hz.
	 */
	public float getBandWidth() {
		return bandWidth;
	}

	/**
	 * Sets the amplitude of the <code>i<sup>th</sup></code> frequency band to <code>a</code>. You can use this to shape the
	 * spectrum before using <code>inverse()</code>.
	 *
	 * @param band      the frequency band to modify
	 * @param amplitude the new amplitude
	 */
	public abstract void setAmplitude(int band, float amplitude);

	/**
	 * Scales the amplitude of the <code>i<sup>th</sup></code> frequency band by <code>s</code>. You can use this to shape the
	 * spectrum before using <code>inverse()</code>.
	 *
	 * @param band   the frequency band to modify
	 * @param factor the scaling factor
	 */
	public abstract void scaleBand(int band, float factor);

	/**
	 * Returns the index of the frequency band that contains the requested frequency.
	 *
	 * @param frequency the frequency you want the index for (in Hz)
	 * @return the index of the frequency band that contains freq
	 */
	public int getBandIndex(float frequency) {

		// special case: freq is lower than the bandwidth of spectrum[0]
		if (frequency < getBandWidth() / 2) {
			return 0;
		}

		// special case: freq is within the bandwidth of spectrum[spectrum.length - 1]
		if (frequency > sampleRate / 2 - getBandWidth() / 2) {
			return spectrum.length - 1;
		}

		// all other cases
		float fraction = frequency / sampleRate;
		return round(size * fraction);
	}

	/**
	 * @param band the index of the band you want to middle frequency of
	 * @return middle frequency of the i<sup>th</sup> band
	 */
	public float getFrequency(int band) {

		var bandWidth = getBandWidth();

		// special case: the width of the first bin is half that of the others.
		// so the center frequency is a quarter of the way.
		if (band == 0) {
			return bandWidth * 0.25f;
		}

		// special case: the width of the last bin is half that of the others.
		if (band == spectrum.length - 1) {

			var lastBinBeginFreq = (sampleRate / 2.0f) - (bandWidth / 2);
			var binHalfWidth = bandWidth * 0.25f;
			return lastBinBeginFreq + binHalfWidth;
		}

		// the center frequency of the ith band is simply i*bw
		// because the first band is half the width of all others.
		// treating it as if it wasn't offsets us to the middle
		// of the band.
		return band * bandWidth;
	}

	/**
	 * @param i which average band you want the center frequency of
	 * @return center frequency of the i<sup>th</sup> average band
	 */
	public float getAverageCenterFrequency(int i) {

		if (whichAverage == LINEAR_AVERAGE) {

			// an average represents a certain number of bands in the spectrum
			var avgWidth = spectrum.length / averages.length;

			// the "center" bin of the average, this is fudgy.
			var centerBinIndex = i * avgWidth + avgWidth / 2;
			return getFrequency(centerBinIndex);

		} else if (whichAverage == LOGARITHMIC_AVERAGE) {

			// which "octave" is this index in?
			var octave = i / avgPerOctave;

			// which band within that octave is this?
			var offset = i % avgPerOctave;
			float lowFreq, hiFreq, freqStep;

			// figure out the low frequency for this octave
			if (octave == 0) {
				lowFreq = 0;
			} else {
				lowFreq = (sampleRate / 2.0f) / (float) pow(2, octaves - octave);
			}

			// and the high frequency for this octave
			hiFreq = (sampleRate / 2.0f) / (float) pow(2, octaves - octave - 1);

			// each average band within the octave will be this big
			freqStep = (hiFreq - lowFreq) / avgPerOctave;

			// figure out the low frequency of the band we care about
			var f = lowFreq + offset * freqStep;

			// the center of the band will be the low plus half the width
			return f + freqStep / 2;
		}

		return 0;
	}

	/**
	 * @param frequency frequency in Hz
	 * @return amplitude the spectrum band containing the given frequency
	 */
	public float getAmplitude(float frequency) {
		return getAmplitude(getBandIndex(frequency));
	}

	/**
	 * Sets the amplitude of the requested frequency in the spectrum to {@code amplitude}.
	 *
	 * @param frequency frequency in Hz
	 * @param amplitude new amplitude
	 */
	public void setAmplitude(float frequency, float amplitude) {
		setAmplitude(getBandIndex(frequency), amplitude);
	}

	/**
	 * Scales the amplitude of the requested frequency by {@code factor}.
	 *
	 * @param frequency frequency in Hz
	 * @param factor    scaling factor
	 */
	public void scale(float frequency, float factor) {
		scaleBand(getBandIndex(frequency), factor);
	}

	/**
	 * @return number of averages currently being calculated
	 */
	public int averageSize() {
		return averages.length;
	}

	/**
	 * @return average magnitudes per band
	 */
	public float[] getAverages() {
		return averages;
	}

	/**
	 * @param index index of the average band
	 * @return value of the requested average band
	 */
	public float getAverage(int index) {
		return averages.length > 0 ? averages[index] : 0.0f;
	}

	/**
	 * Calculate the average amplitude of the frequency band
	 * bounded by {@code lowFrequency} and {@code highFrequency}, inclusive.
	 *
	 * @param lowFrequency  lower bound of the band
	 * @param highFrequency upper bound of the band
	 * @return average amplitude of all spectrum amplitude within the bounds
	 */
	public float getAverageAmplitude(float lowFrequency, float highFrequency) {

		var lowIndex = getBandIndex(lowFrequency);
		var highIndex = getBandIndex(highFrequency);

		var sum = 0.0f;
		var bandCount = highIndex - lowIndex + 1;

		for (var band = lowIndex; band <= highIndex; band++) {
			sum += spectrum[band];
		}

		return sum / bandCount;
	}

	/**
	 * Performs a forward transform on {@code buffer}.
	 *
	 * @param buffer buffer to analyze
	 */
	public abstract void forward(float[] buffer);

	/**
	 * Performs an inverse transform of the frequency spectrum and places the result in {@code buffer}.
	 *
	 * @param buffer buffer to place the result of the inverse transform in
	 */
	public abstract void inverse(float[] buffer);

	/**
	 * Performs an inverse transform of the frequency spectrum
	 * represented by {@code spectrumReal} and {@code spectrumImaginary}
	 * and places the result in {@code buffer}.
	 *
	 * @param spectrumReal      real part of the frequency spectrum
	 * @param spectrumImaginary imaginary part the frequency spectrum
	 * @param buffer            buffer to place the inverse transform in
	 */
	public void inverse(float[] spectrumReal, float[] spectrumImaginary, float[] buffer) {

		setComplex(spectrumReal, spectrumImaginary);
		inverse(buffer);
	}

	/**
	 * @return spectrum of the last {@link #forward(float[])} call
	 */
	public float[] getSpectrum() {
		return spectrum;
	}

	/**
	 * @return real part of the last {@link #forward(float[])} call
	 */
	public float[] getRealPart() {
		return real;
	}

	/**
	 * @return imaginary part of the last {@link #forward(float[])} call
	 */
	public float[] getImaginaryPart() {
		return imaginary;
	}
}
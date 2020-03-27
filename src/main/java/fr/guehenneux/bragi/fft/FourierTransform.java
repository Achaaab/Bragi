package fr.guehenneux.bragi.fft;

import static java.lang.Math.pow;
import static java.lang.Math.round;
import static java.lang.Math.sqrt;
import static java.lang.System.arraycopy;

/**
 * A Fourier Transform is an algorithm that transforms a signal in the time domain, such as a sample buffer, into a signal in the
 * frequency domain, often called the spectrum. The spectrum does not represent individual frequencies, but actually represents
 * frequency bands centered on particular frequencies. The center frequency of each band is usually expressed as a fraction of the
 * sampling rate of the time domain signal and is equal to the index of the frequency band divided by the total number of bands.
 * The total number of frequency bands is usually equal to the length of the time domain signal, but access is only provided to
 * frequency bands with indices less than half the length, because they correspond to frequencies below the <a
 * href="http://en.wikipedia.org/wiki/Nyquist_frequency">Nyquist frequency</a>. In other words, given a signal of length
 * <code>N</code>, there will be <code>N/2</code> frequency bands in the spectrum.
 * <p>
 * As an example, if you construct a FourierTransform with a <code>timeSize</code> of 1024 and and a <code>sampleRate</code> of
 * 44100 Hz, then the spectrum will contain values for frequencies below 22010 Hz, which is the Nyquist frequency (half the sample
 * rate). If you ask for the value of band number 5, this will correspond to a frequency band centered on
 * <code>5/1024 * 44100 = 0.0048828125 * 44100 = 215 Hz</code>. The width of that frequency band is equal to <code>2/1024</code>,
 * expressed as a fraction of the total bandwidth of the spectrum. The total bandwith of the spectrum is equal to the Nyquist
 * frequency, which in this case is 22100, so the bandwidth is equal to about 50 Hz. It is not necessary for you to remember all
 * of these relationships, though it is good to be aware of them. The function <code>getFreq()</code> allows you to query the
 * spectrum with a frequency in Hz and the function <code>getBandWidth()</code> will return the bandwidth in Hz of each frequency
 * band in the spectrum.
 * <p>
 * <b>Usage</b>
 * <p>
 * A typical usage of a FourierTransform is to analyze a signal so that the frequency spectrum may be represented in some way,
 * typically with vertical lines. You could do this in Processing with the following code, where <code>audio</code> is an
 * AudioSource and <code>fft</code> is an FFT (one of the derived classes of FourierTransform).
 *
 * <pre>
 * fft.forward(audio.left);
 * for (int i = 0; i &lt; fft.specSize(); i++) {
 * 	// draw the line for frequency band i, scaling it by 4 so we can see it a bit better
 * 	line(i, height, i, height - fft.getBand(i) * 4);
 * }
 * </pre>
 *
 * <b>Windowing</b>
 * <p>
 * Windowing is the process of shaping the audio samples before transforming them to the frequency domain.
 * The result of using a window is to reduce the noise in the spectrum somewhat.
 * <p>
 * <b>Averages</b>
 * <p>
 * FourierTransform also has functions that allow you to request the creation of an average spectrum. An average spectrum is
 * simply a spectrum with fewer bands than the full spectrum where each average band is the average of the amplitudes of some
 * number of contiguous frequency bands in the full spectrum.
 * <p>
 * <code>linAverages()</code> allows you to specify the number of averages that you want and will group frequency bands into
 * groups of equal number. So if you have a spectrum with 512 frequency bands and you ask for 64 averages, each average will span
 * 8 bands of the full spectrum.
 * <p>
 * <code>logAverages()</code> will group frequency bands by octave and allows you to specify the size of the smallest octave to
 * use (in Hz) and also how many bands to split each octave into. So you might ask for the smallest octave to be 60 Hz and to
 * split each octave into two bands. The result is that the bandwidth of each average is different. One frequency is an octave
 * above another when it's frequency is twice that of the lower frequency. So, 120 Hz is an octave above 60 Hz, 240 Hz is an
 * octave above 120 Hz, and so on. When octaves are split, they are split based on Hz, so if you split the octave 60-120 Hz in
 * half, you will get 60-90Hz and 90-120Hz. You can see how these bandwidths increase as your octave sizes grow. For instance, the
 * last octave will always span <code>sampleRate/4 - sampleRate/2</code>, which in the case of audio sampled at 44100 Hz is
 * 11025-22010 Hz. These logarithmically spaced averages are usually much more useful than the full spectrum or the linearly
 * spaced averages because they map more directly to how humans perceive sound.
 * <p>
 * <code>calcAvg()</code> allows you to specify the frequency band you want an average calculated for. You might ask for 60-500Hz
 * and this function will group together the bands from the full spectrum that fall into that range and average their amplitudes
 * for you.
 * <p>
 * If you don't want any averages calculated, then you can call <code>noAverages()</code>. This will not impact your ability to
 * use <code>calcAvg()</code>, it will merely prevent the object from calculating an average array every time you use
 * <code>forward()</code>.
 * <p>
 * <b>Inverse Transform</b>
 * <p>
 * FourierTransform also supports taking the inverse transform of a spectrum. This means that a frequency spectrum will be
 * transformed into a time domain signal and placed in a provided sample buffer. The length of the time domain signal will be
 * <code>timeSize()</code> long. The <code>set</code> and <code>scale</code> functions allow you the ability to shape the spectrum
 * already stored in the object before taking the inverse transform. You might use these to filter frequencies in a spectrum or
 * modify it in some other way.
 *
 * @author Damien Di Fede
 * @see <a href="http://www.dspguide.com/ch8.htm">The Discrete Fourier Transform</a>
 */
public abstract class FourierTransform {

	protected static final int LINAVG = 2;
	protected static final int LOGAVG = 3;
	protected static final int NOAVG = 4;

	protected int size;
	protected float sampleRate;
	protected float bandWidth;
	protected float[] real;
	protected float[] imaginary;
	protected float[] spectrum;
	protected float[] averages;
	protected int whichAverage;
	protected int octaves;
	protected int avgPerOctave;

	protected Window window;

	/**
	 * Construct a FourierTransform that will analyze sample buffers that are <code>ts</code> samples long
	 * and contain samples with a <code>sr</code> sample rate.
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
	 * allocating real, imag, and spectrum are the responsibility of derived classes
	 * because the size of the arrays will depend on the implementation being used
	 * this enforces that responsibility
	 */
	protected abstract void allocateArrays();

	/**
	 * @param r
	 * @param i
	 */
	protected void setComplex(float[] r, float[] i) {

		if (real.length != r.length && imaginary.length != i.length) {

			throw new IllegalArgumentException("This won't work");

		} else {

			arraycopy(r, 0, real, 0, r.length);
			arraycopy(i, 0, imaginary, 0, i.length);
		}
	}

	/**
	 * fill the spectrum array with the amps of the data in real and imag
	 * used so that this class can handle creating the average array
	 * and also do spectrum shaping if necessary
	 */
	protected void fillSpectrum() {

		for (int i = 0; i < spectrum.length; i++) {
			spectrum[i] = (float) sqrt(real[i] * real[i] + imaginary[i] * imaginary[i]);
		}

		if (whichAverage == LINAVG) {

			int averageWidth = spectrum.length / averages.length;

			for (var i = 0; i < averages.length; i++) {

				float avg = 0;
				int j;

				for (j = 0; j < averageWidth; j++) {

					int offset = j + i * averageWidth;
					if (offset < spectrum.length) {
						avg += spectrum[offset];
					} else {
						break;
					}
				}

				avg /= j + 1;
				averages[i] = avg;
			}

		} else if (whichAverage == LOGAVG) {

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

		averages = new float[0];
		whichAverage = NOAVG;
	}

	/**
	 * Sets the number of averages used when computing the spectrum and spaces the averages in a linear manner. In other words,
	 * each average band will be <code>specSize() / numAvg</code> bands wide.
	 *
	 * @param numAvg how many averages to compute
	 */
	public void linAverages(int numAvg) {

		if (numAvg > spectrum.length / 2) {

			throw new IllegalArgumentException(
					"The number of averages for this transform can be at most " +
							spectrum.length / 2 + ".");

		} else {

			averages = new float[numAvg];
		}

		whichAverage = LINAVG;
	}

	/**
	 * Sets the number of averages used when computing the spectrum based on the minimum bandwidth for an octave and the number of
	 * bands per octave. For example, with audio that has a sample rate of 44100 Hz, <code>logAverages(11, 1)</code> will result in
	 * 12 averages, each corresponding to an octave, the first spanning 0 to 11 Hz. To ensure that each octave band is a full
	 * octave, the number of octaves is computed by dividing the Nyquist frequency by two, and then the result of that by two, and
	 * so on. This means that the actual bandwidth of the lowest octave may not be exactly the value specified.
	 *
	 * @param minBandwidth   the minimum bandwidth used for an octave
	 * @param bandsPerOctave how many bands to split each octave into
	 */
	public void logAverages(int minBandwidth, int bandsPerOctave) {

		float nyq = sampleRate / 2;
		octaves = 1;

		while ((nyq /= 2) > minBandwidth) {
			octaves++;
		}

		avgPerOctave = bandsPerOctave;
		averages = new float[octaves * bandsPerOctave];
		whichAverage = LOGAVG;
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
	public int timeSize() {
		return size;
	}

	/**
	 * Returns the size of the spectrum created by this transform. In other words, the number of frequency bands produced by this
	 * transform. This is typically equal to <code>timeSize()/2 + 1</code>, see above for an explanation.
	 *
	 * @return the size of the spectrum
	 */
	public int specSize() {
		return spectrum.length;
	}

	/**
	 * Returns the amplitude of the requested frequency band.
	 *
	 * @param i the index of a frequency band
	 * @return the amplitude of the requested frequency band
	 */
	public float getBand(int i) {

		if (i < 0) i = 0;
		if (i > spectrum.length - 1) i = spectrum.length - 1;

		return spectrum[i];
	}

	/**
	 * Returns the width of each frequency band in the spectrum (in Hz). It should be noted that the bandwidth of the first and
	 * last frequency bands is half as large as the value returned by this function.
	 *
	 * @return the width of each frequency band in Hz.
	 */
	public float getBandWidth() {
		return bandWidth;
	}

	/**
	 * Sets the amplitude of the <code>i<sup>th</sup></code> frequency band to <code>a</code>. You can use this to shape the
	 * spectrum before using <code>inverse()</code>.
	 *
	 * @param i the frequency band to modify
	 * @param a the new amplitude
	 */
	public abstract void setBand(int i, float a);

	/**
	 * Scales the amplitude of the <code>i<sup>th</sup></code> frequency band by <code>s</code>. You can use this to shape the
	 * spectrum before using <code>inverse()</code>.
	 *
	 * @param i the frequency band to modify
	 * @param s the scaling factor
	 */
	public abstract void scaleBand(int i, float s);

	/**
	 * Returns the index of the frequency band that contains the requested frequency.
	 *
	 * @param freq the frequency you want the index for (in Hz)
	 * @return the index of the frequency band that contains freq
	 */
	public int getFrequencyIndex(float freq) {

		// special case: freq is lower than the bandwidth of spectrum[0]
		if (freq < getBandWidth() / 2) return 0;

		// special case: freq is within the bandwidth of spectrum[spectrum.length - 1]
		if (freq > sampleRate / 2 - getBandWidth() / 2) return spectrum.length - 1;

		// all other cases
		float fraction = freq / (float) sampleRate;
		return round(size * fraction);
	}

	/**
	 * @param i the index of the band you want to middle frequency of
	 * @return middle frequency of the i<sup>th</sup> band
	 */
	public float indexToFreq(int i) {

		float bw = getBandWidth();

		// special case: the width of the first bin is half that of the others.
		// so the center frequency is a quarter of the way.
		if (i == 0) return bw * 0.25f;

		// special case: the width of the last bin is half that of the others.
		if (i == spectrum.length - 1) {

			float lastBinBeginFreq = (sampleRate / 2.0f) - (bw / 2);
			float binHalfWidth = bw * 0.25f;
			return lastBinBeginFreq + binHalfWidth;
		}

		// the center frequency of the ith band is simply i*bw
		// because the first band is half the width of all others.
		// treating it as if it wasn't offsets us to the middle
		// of the band.
		return i * bw;
	}

	/**
	 * @param i which average band you want the center frequency of
	 * @return center frequency of the i<sup>th</sup> average band
	 */
	public float getAverageCenterFrequency(int i) {

		if (whichAverage == LINAVG) {

			// an average represents a certain number of bands in the spectrum
			int avgWidth = spectrum.length / averages.length;

			// the "center" bin of the average, this is fudgy.
			int centerBinIndex = i * avgWidth + avgWidth / 2;
			return indexToFreq(centerBinIndex);

		} else if (whichAverage == LOGAVG) {

			// which "octave" is this index in?
			int octave = i / avgPerOctave;

			// which band within that octave is this?
			int offset = i % avgPerOctave;
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
			float f = lowFreq + offset * freqStep;

			// the center of the band will be the low plus half the width
			return f + freqStep / 2;
		}

		return 0;
	}

	/**
	 * Gets the amplitude of the requested frequency in the spectrum.
	 *
	 * @param freq frequency in Hz
	 * @return amplitude of the frequency in the spectrum
	 */
	public float getFreq(float freq) {
		return getBand(getFrequencyIndex(freq));
	}

	/**
	 * Sets the amplitude of the requested frequency in the spectrum to <code>a</code>.
	 *
	 * @param freq the frequency in Hz
	 * @param a    the new amplitude
	 */
	public void setFreq(float freq, float a) {
		setBand(getFrequencyIndex(freq), a);
	}

	/**
	 * Scales the amplitude of the requested frequency by <code>a</code>.
	 *
	 * @param freq the frequency in Hz
	 * @param s    the scaling factor
	 */
	public void scaleFreq(float freq, float s) {
		scaleBand(getFrequencyIndex(freq), s);
	}

	/**
	 * @return number of averages currently being calculated
	 */
	public int avgSize() {
		return averages.length;
	}

	/**
	 * @return average magnitudes per band
	 */
	public float[] getAverages() {
		return averages;
	}

	/**
	 * Gets the value of the <code>i<sup>th</sup></code> average.
	 *
	 * @param i the average you want the value of
	 * @return the value of the requested average band
	 */
	public float getAvg(int i) {
		return averages.length > 0 ? averages[i] : 0;
	}

	/**
	 * Calculate the average amplitude of the frequency band bounded by <code>lowFreq</code> and <code>hiFreq</code>, inclusive.
	 *
	 * @param lowFrequency the lower bound of the band
	 * @param highFrequency the upper bound of the band
	 * @return average amplitude of all spectrum amplitude within the bounds
	 */
	public float getAverageAmplitude(float lowFrequency, float highFrequency) {

		var lowIndex = getFrequencyIndex(lowFrequency);
		var highIndex = getFrequencyIndex(highFrequency);

		var sum = 0.0f;
		var bandCount = highIndex - lowIndex + 1;

		for (var band = lowIndex; band <= highIndex; band++) {
			sum += spectrum[band];
		}

		return sum / bandCount;
	}

	/**
	 * Performs a forward transform on <code>buffer</code>.
	 *
	 * @param buffer the buffer to analyze
	 */
	public abstract void forward(float[] buffer);

	/**
	 * Performs a forward transform on values in <code>buffer</code>.
	 *
	 * @param buffer  the buffer of samples
	 * @param startAt the index to start at in the buffer. there must be at least timeSize() samples between the starting index and
	 *                the end of the buffer. If there aren't, an error will be issued and the operation will not be performed.
	 */
	public void forward(float[] buffer, int startAt) {

		if (buffer.length - startAt < size) {

			throw new IllegalArgumentException(
					"FourierTransform.forward: not enough samples in the buffer between " +
							startAt + " and " + buffer.length +
							" to perform a transform.");
		}

		// copy the section of samples we want to analyze
		var section = new float[size];
		arraycopy(buffer, startAt, section, 0, section.length);
		forward(section);
	}

	/**
	 * Performs an inverse transform of the frequency spectrum and places the result in <code>buffer</code>.
	 *
	 * @param buffer the buffer to place the result of the inverse transform in
	 */
	public abstract void inverse(float[] buffer);

	/**
	 * Performs an inverse transform of the frequency spectrum represented by freqReal and freqImag and places the result in
	 * buffer.
	 *
	 * @param freqReal the real part of the frequency spectrum
	 * @param freqImag the imaginary part the frequency spectrum
	 * @param buffer   the buffer to place the inverse transform in
	 */
	public void inverse(float[] freqReal, float[] freqImag, float[] buffer) {

		setComplex(freqReal, freqImag);
		inverse(buffer);
	}

	/**
	 * @return spectrum of the last FourierTransform.forward() call.
	 */
	public float[] getSpectrum() {
		return spectrum;
	}

	/**
	 * @return the real part of the last FourierTransform.forward() call.
	 */
	public float[] getRealPart() {
		return real;
	}

	/**
	 * @return the imaginary part of the last FourierTransform.forward() call.
	 */
	public float[] getImaginaryPart() {
		return imaginary;
	}
}
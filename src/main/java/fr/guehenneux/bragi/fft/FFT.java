package fr.guehenneux.bragi.fft;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.fma;
import static java.lang.Math.sin;

/**
 * FFT stands for Fast Fourier Transform. It is an efficient way to calculate the Complex Discrete Fourier Transform. There is not
 * much to say about this class other than the fact that when you want to analyze the spectrum of an audio buffer you will almost
 * always use this class. One restriction of this class is that the audio buffers you want to analyze must have a length that is a
 * power of two. If you try to construct an FFT with a <code>timeSize</code> that is not a power of two, an
 * IllegalArgumentException will be thrown.
 *
 * @author Damien Di Fede
 * @see FourierTransform
 * @see <a href="http://www.dspguide.com/ch12.htm">The Fast Fourier Transform</a>
 */
public class FFT extends FourierTransform {

	private int[] reverse;

	private float[] sin;
	private float[] cos;

	/**
	 * Constructs an FFT that will accept sample buffers that are <code>timeSize</code> long and have been recorded with a sample
	 * rate of <code>sampleRate</code>. <code>timeSize</code> <em>must</em> be a power of two. This will throw an exception if it
	 * is not.
	 *
	 * @param timeSize   the length of the sample buffers you will be analyzing
	 * @param sampleRate the sample rate of the audio you will be analyzing
	 */
	public FFT(int timeSize, float sampleRate) {

		super(timeSize, sampleRate);

		if ((timeSize & (timeSize - 1)) != 0) {
			throw new IllegalArgumentException("FFT: timeSize must be a power of two.");
		}

		buildReverseTable();
		buildTrigTables();
	}

	@Override
	protected void allocateArrays() {

		spectrum = new float[size / 2 + 1];
		real = new float[size];
		imag = new float[size];
	}

	@Override
	public void scaleBand(int i, float s) {

		if (s < 0) {
			throw new IllegalArgumentException("Can't scale a frequency band by a negative value.");
		}

		if (spectrum[i] != 0) {

			real[i] /= spectrum[i];
			imag[i] /= spectrum[i];
			spectrum[i] *= s;
			real[i] *= spectrum[i];
			imag[i] *= spectrum[i];
		}

		if (i != 0 && i != size / 2) {

			real[size - i] = real[i];
			imag[size - i] = -imag[i];
		}
	}

	@Override
	public void setBand(int i, float a) {

		if (a < 0) {
			throw new IllegalArgumentException("Can't set a frequency band to a negative value.");
		}

		if (real[i] == 0 && imag[i] == 0) {

			real[i] = a;
			spectrum[i] = a;

		} else {

			real[i] /= spectrum[i];
			imag[i] /= spectrum[i];
			spectrum[i] = a;
			real[i] *= spectrum[i];
			imag[i] *= spectrum[i];
		}

		if (i != 0 && i != size / 2) {

			real[size - i] = real[i];
			imag[size - i] = -imag[i];
		}
	}

	/**
	 * performs an in-place fft on the data in the real and imag arrays
	 * bit reversing is not necessary as the data will already be bit reversed
	 */
	private void fft() {

		for (var halfSize = 1; halfSize < real.length; halfSize *= 2) {

			var phaseShiftStepR = cos[halfSize];
			var phaseShiftStepI = sin[halfSize];

			// current phase shift

			var currentPhaseShiftR = 1.0f;
			var currentPhaseShiftI = 0.0f;

			for (var fftStep = 0; fftStep < halfSize; fftStep++) {

				for (var i = fftStep; i < real.length; i += 2 * halfSize) {

					var off = i + halfSize;
					var tr = fma(currentPhaseShiftR, real[off], - currentPhaseShiftI * imag[off]);
					var ti = fma(currentPhaseShiftR, imag[off], currentPhaseShiftI * real[off]);

					real[off] = real[i] - tr;
					imag[off] = imag[i] - ti;
					real[i] += tr;
					imag[i] += ti;
				}

				var tmpR = currentPhaseShiftR;

				currentPhaseShiftR = fma(tmpR, phaseShiftStepR, -currentPhaseShiftI * phaseShiftStepI);
				currentPhaseShiftI = fma(tmpR, phaseShiftStepI, currentPhaseShiftI * phaseShiftStepR);
			}
		}
	}

	@Override
	public void forward(float[] buffer) {

		if (buffer.length != size) {
			throw new IllegalArgumentException(
					"FFT.forward: The length of the passed sample buffer must be equal to timeSize().");
		}

		window.apply(buffer);

		// copy samples to real/imag in bit-reversed order
		bitReverseSamples(buffer);

		// perform the fft
		fft();

		// fill the spectrum buffer with amplitudes
		fillSpectrum();
	}

	/**
	 * Performs a forward transform on the passed buffers.
	 *
	 * @param buffReal the real part of the time domain signal to transform
	 * @param buffImag the imaginary part of the time domain signal to transform
	 */
	public void forward(float[] buffReal, float[] buffImag) {

		if (buffReal.length != size || buffImag.length != size) {
			throw new IllegalArgumentException(
					"FFT.forward: The length of the passed buffers must be equal to timeSize().");
		}

		setComplex(buffReal, buffImag);
		bitReverseComplex();
		fft();
		fillSpectrum();
	}

	@Override
	public void inverse(float[] buffer) {

		if (buffer.length > real.length) {
			throw new IllegalArgumentException("FFT.inverse: the passed array's length must equal FFT.timeSize().");
		}

		// conjugate
		for (int i = 0; i < size; i++) {
			imag[i] *= -1;
		}

		bitReverseComplex();
		fft();

		// copy the result in real into buffer, scaling as we do
		for (int i = 0; i < buffer.length; i++) {
			buffer[i] = real[i] / real.length;
		}
	}

	/**
	 *
	 */
	private void buildReverseTable() {

		int N = size;
		reverse = new int[N];

		// set up the bit reversing table
		reverse[0] = 0;

		for (int limit = 1, bit = N / 2; limit < N; limit <<= 1, bit >>= 1) {

			for (int i = 0; i < limit; i++) {
				reverse[i + limit] = reverse[i] + bit;
			}
		}
	}

	/**
	 * copies the values in the samples array into the real array
	 * in bit reversed order. the imag array is filled with zeros.
	 *
	 * @param samples
	 */
	private void bitReverseSamples(float[] samples) {

		for (int i = 0; i < samples.length; i++) {

			real[i] = samples[reverse[i]];
			imag[i] = 0.0f;
		}
	}

	/**
	 * bit reverse real[] and imag[]
	 */
	private void bitReverseComplex() {

		float[] revReal = new float[real.length];
		float[] revImag = new float[imag.length];

		for (int i = 0; i < real.length; i++) {

			revReal[i] = real[reverse[i]];
			revImag[i] = imag[reverse[i]];
		}

		real = revReal;
		imag = revImag;
	}

	/**
	 *
	 */
	private void buildTrigTables() {

		sin = new float[size];
		cos = new float[size];

		for (int t = 0; t < size; t++) {

			sin[t] = (float) sin(-PI / t);
			cos[t] = (float) cos(-PI / t);
		}
	}
}
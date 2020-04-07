package com.github.achaaab.bragi.dsp.fft;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.fma;
import static java.lang.Math.sin;

/**
 * The Fast Fourier Transform (FFT) is an efficient way to calculate the Complex Discrete Fourier Transform.
 * One restriction of this implementation is that the audio buffers you want to analyze must have a length that is a
 * power of two. FFT has a O(n.log(n)) time complexity while a naive implementation has a O(n²) time complexity.
 *
 * @author Damien Di Fede
 * @author Jonathan Guéhenneux
 * @see <a href="http://www.dspguide.com/ch12.htm">The Fast Fourier Transform</a>
 * @since 0.1.0
 */
public class FastFourierTransform extends FourierTransform {

	private int[] reverse;
	private float[] sin;
	private float[] cos;

	/**
	 * Constructs an FFT that will accept sample buffers that are <code>timeSize</code> long and have been recorded with a sample
	 * rate of <code>sampleRate</code>. <code>timeSize</code> <em>must</em> be a power of two.
	 *
	 * @param timeSize   the length of the sample buffers you will be analyzing
	 * @param sampleRate the sample rate of the audio you will be analyzing
	 */
	public FastFourierTransform(int timeSize, float sampleRate) {

		super(timeSize, sampleRate);

		buildReverseTable();
		memoizeSineAndCosine();
	}

	@Override
	protected void allocateArrays() {

		spectrum = new float[size / 2 + 1];
		real = new float[size];
		imaginary = new float[size];
	}

	@Override
	public void scaleBand(int band, float factor) {

		if (spectrum[band] != 0) {

			spectrum[band] *= factor;
			real[band] *= factor;
			imaginary[band] *= factor;
		}

		if (band != 0 && band != size / 2) {

			real[size - band] = real[band];
			imaginary[size - band] = -imaginary[band];
		}
	}

	@Override
	public void setAmplitude(int band, float amplitude) {

		if (real[band] == 0 && imaginary[band] == 0) {

			real[band] = amplitude;
			spectrum[band] = amplitude;

		} else {

			real[band] /= spectrum[band];
			imaginary[band] /= spectrum[band];
			spectrum[band] = amplitude;
			real[band] *= spectrum[band];
			imaginary[band] *= spectrum[band];
		}

		if (band != 0 && band != size / 2) {

			real[size - band] = real[band];
			imaginary[size - band] = -imaginary[band];
		}
	}

	/**
	 * Performs an in-place FFT on the data in the real and imaginary arrays.
	 * Bit reversing is not necessary as the data will already be bit reversed.
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
					var tr = fma(currentPhaseShiftR, real[off], - currentPhaseShiftI * imaginary[off]);
					var ti = fma(currentPhaseShiftR, imaginary[off], currentPhaseShiftI * real[off]);

					real[off] = real[i] - tr;
					imaginary[off] = imaginary[i] - ti;
					real[i] += tr;
					imaginary[i] += ti;
				}

				var tmpR = currentPhaseShiftR;

				currentPhaseShiftR = fma(tmpR, phaseShiftStepR, -currentPhaseShiftI * phaseShiftStepI);
				currentPhaseShiftI = fma(tmpR, phaseShiftStepI, currentPhaseShiftI * phaseShiftStepR);
			}
		}
	}

	@Override
	public void forward(float[] buffer) {

		window.apply(buffer);

		// copy samples to real and imaginary in bit-reversed order
		bitReverseSamples(buffer);

		// perform the FFT
		fft();

		// fill the spectrum buffer with amplitudes
		fillSpectrum();
	}

	/**
	 * Performs a forward transform on the passed buffers.
	 *
	 * @param real the real part of the time domain signal to transform
	 * @param imaginary the imaginary part of the time domain signal to transform
	 */
	public void forward(float[] real, float[] imaginary) {

		setComplex(real, imaginary);
		bitReverseComplex();
		fft();
		fillSpectrum();
	}

	@Override
	public void inverse(float[] buffer) {

		// conjugate
		for (var i = 0; i < size; i++) {
			imaginary[i] *= -1;
		}

		bitReverseComplex();
		fft();

		// copy the result in real into buffer, scaling as we do
		for (var i = 0; i < buffer.length; i++) {
			buffer[i] = real[i] / real.length;
		}
	}

	/**
	 * Set up the bit reversing table.
	 */
	private void buildReverseTable() {

		reverse = new int[size];

		int limit = 1;
		int bits = size >> 1;

		while (limit < size) {

			for (var index = 0; index < limit; index++) {
				reverse[limit + index] = reverse[index] + bits;
			}

			limit <<= 1;
			bits >>= 1;
		}
	}

	/**
	 * Copies the values in the samples array into the real array in bit reversed order.
	 * The imaginary array is filled with zeros.
	 *
	 * @param samples samples to copy
	 */
	private void bitReverseSamples(float[] samples) {

		for (var index = 0; index < size; index++) {

			real[index] = samples[reverse[index]];
			imaginary[index] = 0.0f;
		}
	}

	/**
	 * Bit reverse real and imaginary.
	 */
	private void bitReverseComplex() {

		var reversedReal = new float[size];
		var reversedImaginary = new float[size];

		for (var index = 0; index < size; index++) {

			reversedReal[index] = real[reverse[index]];
			reversedImaginary[index] = imaginary[reverse[index]];
		}

		real = reversedReal;
		imaginary = reversedImaginary;
	}

	/**
	 * Memoize {@code sin(-π/t)} and {@code cos(-π/t)}.
	 */
	private void memoizeSineAndCosine() {

		sin = new float[size];
		cos = new float[size];

		for (var t = 0; t < size; t++) {

			sin[t] = (float) sin(-PI / t);
			cos[t] = (float) cos(-PI / t);
		}
	}
}
package com.github.achaaab.bragi.dsp.fft;

/**
 * A window is an apodization function.
 * It is a mathematical function that is zero-valued outside of some chosen interval,
 * normally symmetric around the middle of the interval,
 * usually near a maximum in the middle and usually tapering away from the middle.
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.5
 */
public interface Window {

	/**
	 * Apply window function on given samples.
	 *
	 * @param samples samples on which to apply window
	 */
	void apply(float[] samples);
}
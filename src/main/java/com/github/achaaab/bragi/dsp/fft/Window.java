package com.github.achaaab.bragi.dsp.fft;

/**
 * A window is an apodization function.
 * It is a mathematical function that is zero-valued outside some chosen interval,
 * normally symmetric around the middle of the interval,
 * usually near a maximum in the middle and usually tapering away from the middle.
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.5
 */
public interface Window {

	/**
	 * Applies window function on given samples.
	 *
	 * @param samples samples on which to apply window
	 * @since 0.2.0
	 */
	void apply(float[] samples);
}

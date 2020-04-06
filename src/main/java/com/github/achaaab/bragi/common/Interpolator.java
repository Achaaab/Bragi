package com.github.achaaab.bragi.common;

/**
 * @author Jonathan Guéhenneux
 * @since 0.1.7
 */
public interface Interpolator {

	Interpolator CUBIC_HERMITE_SPLINE = new CubicHermiteSpline();

	/**
	 * @param samples samples to interpolate
	 * @param sourceSampleRate source sampling rate
	 * @param targetSampleRate target sampling rate
	 * @return interpolated samples
	 */
	float[] interpolate(float[] samples, float sourceSampleRate, float targetSampleRate);
}
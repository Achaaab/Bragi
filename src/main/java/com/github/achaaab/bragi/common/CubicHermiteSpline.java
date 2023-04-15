package com.github.achaaab.bragi.common;

import static java.lang.Math.round;

/**
 * @author Jonathan Guéhenneux
 * @since 0.1.7
 */
public class CubicHermiteSpline implements Interpolator {

	/**
	 * @see #CUBIC_HERMITE_SPLINE
	 * @since 0.2.0
	 */
	CubicHermiteSpline() {

	}

	@Override
	public float[] interpolate(float[] sourceSamples, float sourceSampleRate, float targetSampleRate) {

		var sourceSampleCount = sourceSamples.length;
		var targetSampleCount = round(sourceSampleCount * targetSampleRate / sourceSampleRate);
		var targetSamples = new float[targetSampleCount];

		var indexRatio = (float) (sourceSampleCount - 1) / targetSampleCount;

		for (var targetSampleIndex = 0; targetSampleIndex < targetSampleCount; targetSampleIndex++) {

			var i = targetSampleIndex * indexRatio;
			var i0 = (int) i;
			var x = i - i0;

			var y0 = sourceSamples[i0];

			if (x == 0) {

				targetSamples[targetSampleIndex] = y0;

			} else {

				var y1 = sourceSamples[i0 + 1];

				if (i0 == 0 || i0 == sourceSampleCount - 2) {

					// linear interpolation
					targetSamples[targetSampleIndex] = y0 + x * (y1 - y0);

				} else {

					// cubic Hermite spline
					var ym1 = sourceSamples[i0 - 1];
					var y2 = sourceSamples[i0 + 2];

					var c1 = (y1 - ym1) / 2;
					var t0 = y0 - y1;
					var t1 = c1 + t0;
					var c3 = t1 + t0 + (y2 - y0) / 2;
					var c2 = t1 + c3;

					targetSamples[targetSampleIndex] = ((c3 * x - c2) * x + c1) * x + y0;
				}
			}
		}

		return targetSamples;
	}
}

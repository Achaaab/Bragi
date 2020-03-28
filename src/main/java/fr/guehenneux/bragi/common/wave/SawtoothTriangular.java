package fr.guehenneux.bragi.common.wave;

import fr.guehenneux.bragi.common.Normalizer;

import static java.lang.Math.fma;

/**
 * A sawtooth-triangular waveform increases from minimal voltage to maximal voltage
 * in a fraction of the period. This fraction is called "peak fraction".
 * <p>
 * Then it drops to a computed voltage defined by "drop fraction" which is a fraction of the signal amplitude.
 * From this drop voltage, the signal decreases to minimal voltage.
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.6
 */
public class SawtoothTriangular extends BoundedWaveform {

	public static final SawtoothTriangular INSTANCE = new SawtoothTriangular("Sawtooth-Triangular", 0.75f, 0.4f);

	private float peakFraction;

	private Normalizer increaseNormalizer;
	private Normalizer decreaseNormalizer;

	/**
	 * @param peakFraction percentage of the period where the signal reach the maximum voltage
	 * @param dropFraction percentage of amplitude to which the signal drops after the peak
	 */
	public SawtoothTriangular(String name, float peakFraction, float dropFraction) {

		super(name);

		this.peakFraction = peakFraction;

		var dropVoltage = fma(dropFraction, amplitude, minimum);

		increaseNormalizer = new Normalizer(0.0f, peakFraction, minimum, maximum);
		decreaseNormalizer = new Normalizer(peakFraction, 1.0f, dropVoltage, minimum);
	}

	@Override
	public float getSample(double periodFraction) {

		var normalizer = periodFraction <= peakFraction ? increaseNormalizer : decreaseNormalizer;
		return normalizer.normalize(periodFraction);
	}
}
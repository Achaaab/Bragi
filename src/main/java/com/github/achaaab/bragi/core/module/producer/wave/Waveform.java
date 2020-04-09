package com.github.achaaab.bragi.core.module.producer.wave;

/**
 * @author Jonathan Guéhenneux
 * @since 0.1.0
 */
public interface Waveform {

	Waveform SINE = new Sine();
	Waveform TRIANGLE = new Triangle();
	Waveform SAWTOOTH = new Sawtooth();
	Waveform REVERSE_SAWTOOTH = new ReverseSawtooth();
	Waveform SAWTOOTH_TRIANGULAR = new SawtoothTriangular("Sawtooth-Triangular", 0.75f, 0.40f);
	Waveform NAIVE_SQUARE = new NaivePulse("Naive Square", 0.5);
	Waveform NAIVE_PULSE_4 = new NaivePulse("Naive Rectangle 1/4", 0.25);
	Waveform NAIVE_PULSE_8 = new NaivePulse("Rectangle 1/8", 0.125);
	Waveform SQUARE = new BandLimitedSquare();

	Waveform[] INSTANCES = {
			SINE,
			TRIANGLE,
			SAWTOOTH,
			REVERSE_SAWTOOTH,
			SAWTOOTH_TRIANGULAR,
			NAIVE_SQUARE,
			NAIVE_PULSE_4,
			NAIVE_PULSE_8,
			SQUARE
	};

	float LOWER_PEAK = 0.0f;
	float UPPER_PEAK = 1.0f;
	float AMPLITUDE = UPPER_PEAK - LOWER_PEAK;
	double HALF_PERIOD = 0.5;

	/**
	 * Computes 1 sample.
	 *
	 * @param periodFraction {@code periodFraction ∈ [0.0, 1.0[}
	 * @return sample at given fraction of waveform period
	 * @since 0.1.7
	 */
	float getSample(double periodFraction);
}
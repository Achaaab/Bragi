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
	Waveform SQUARE = new Pulse("Square", 0.5);
	Waveform PULSE_4 = new Pulse("Rectangle 1/4", 0.25);
	Waveform PULSE_8 = new Pulse("Rectangle 1/8", 0.125);
	Waveform ANALOG_SQUARE = new BandLimitedSquare();

	Waveform[] INSTANCES = {
			SINE,
			TRIANGLE,
			SAWTOOTH,
			REVERSE_SAWTOOTH,
			SAWTOOTH_TRIANGULAR,
			SQUARE,
			PULSE_4,
			PULSE_8,
			ANALOG_SQUARE };

	float LOWER_PEAK = 0.0f;
	float UPPER_PEAK = 1.0f;
	float AMPLITUDE = UPPER_PEAK - LOWER_PEAK;
	double HALF_PERIOD = 0.5;

	/**
	 * Computes 1 sample.
	 *
	 * @param periodFraction {@code periodFraction ∈ [0.0, 1.0[}
	 * @return sample at given fraction of waveform period
	 * @since 0.2.0
	 */
	float getSample(double periodFraction);
}

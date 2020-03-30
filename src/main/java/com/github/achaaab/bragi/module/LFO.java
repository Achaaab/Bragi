package com.github.achaaab.bragi.module;

import com.github.achaaab.bragi.common.Settings;
import com.github.achaaab.bragi.common.wave.Waveform;
import com.github.achaaab.bragi.gui.module.LFOView;
import org.slf4j.Logger;

import static com.github.achaaab.bragi.common.wave.Waveform.SINE;
import static javax.swing.SwingUtilities.invokeLater;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Low Frequency Oscillator
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.9
 */
public class LFO extends Oscillator {

	private static final Logger LOGGER = getLogger(LFO.class);

	public static final String DEFAULT_NAME = "lfo";
	public static final Waveform INITIAL_WAVEFORM = SINE;
	public static final double INITIAL_FREQUENCY = 440.0 / (1 << 6);
	public static final float INITIAL_LOWER_PEAK = -1.0f;
	public static final float INITIAL_UPPER_PEAK = 0.0f;

	/**
	 * Creates an LFO with default name.
	 *
	 * @see #DEFAULT_NAME
	 * @since 0.0.9
	 */
	public LFO() {
		this(DEFAULT_NAME);
	}

	/**
	 * @param name name of the LFO
	 */
	public LFO(String name) {

		super(name, INITIAL_WAVEFORM, INITIAL_FREQUENCY);

		setLowerPeak(INITIAL_LOWER_PEAK);
		setUpperPeak(INITIAL_UPPER_PEAK);

		invokeLater(() -> new LFOView(this));

		start();
	}

	@Override
	public int compute() throws InterruptedException {

		var sampleCount = Settings.INSTANCE.chunkSize();

		var samples = wave.getSamples(0, null, sampleCount);

		output.write(samples);

		return sampleCount;
	}
}
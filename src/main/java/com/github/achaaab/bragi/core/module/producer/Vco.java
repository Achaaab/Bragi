package com.github.achaaab.bragi.core.module.producer;

import com.github.achaaab.bragi.common.Settings;
import com.github.achaaab.bragi.core.connection.Input;
import com.github.achaaab.bragi.core.module.ModuleCreationException;
import com.github.achaaab.bragi.core.module.producer.wave.Waveform;
import com.github.achaaab.bragi.gui.module.VcoView;
import org.slf4j.Logger;

import java.lang.reflect.InvocationTargetException;

import static com.github.achaaab.bragi.core.module.producer.wave.Waveform.SINE;
import static javax.swing.SwingUtilities.invokeAndWait;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Voltage-Controlled Oscillator
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.9
 */
public class Vco extends Oscillator {

	private static final Logger LOGGER = getLogger(Vco.class);

	public static final String DEFAULT_NAME = "vco";
	public static final Waveform INITIAL_WAVEFORM = SINE;
	public static final double BASE_FREQUENCY = 440;

	private int octave;

	private final Input modulation;

	/**
	 * Creates a VCO with default name.
	 *
	 * @see #DEFAULT_NAME
	 * @since 0.0.9
	 */
	public Vco() {
		this(DEFAULT_NAME);
	}

	/**
	 * @param name name of the VCO
	 * @since 0.2.0
	 */
	public Vco(String name) {

		super(name, INITIAL_WAVEFORM, BASE_FREQUENCY);

		modulation = addSecondaryInput(name + "_modulation");

		octave = 0;

		try {
			invokeAndWait(() -> view = new VcoView(this));
		} catch (InterruptedException | InvocationTargetException cause) {
			throw new ModuleCreationException(cause);
		}
	}

	@Override
	public int compute() throws InterruptedException {

		var sampleCount = Settings.INSTANCE.chunkSize();
		var modulationSamples = modulation.read();
		var samples = wave.getSamples(octave, modulationSamples, sampleCount);
		output.write(samples);

		return sampleCount;
	}

	/**
	 * @return modulation input
	 * @since 0.2.0
	 */
	public Input modulation() {
		return modulation;
	}

	/**
	 * @return octave adjustment
	 * @since 0.2.0
	 */
	public int getOctave() {
		return octave;
	}

	/**
	 * @param octave octave adjustment
	 * @since 0.2.0
	 */
	public void setOctave(int octave) {
		this.octave = octave;
	}
}

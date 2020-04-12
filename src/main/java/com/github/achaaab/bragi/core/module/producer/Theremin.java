package com.github.achaaab.bragi.core.module.producer;

import com.github.achaaab.bragi.common.Settings;
import com.github.achaaab.bragi.core.connection.Output;
import com.github.achaaab.bragi.core.module.Module;
import com.github.achaaab.bragi.core.module.ModuleCreationException;
import com.github.achaaab.bragi.gui.module.ThereminView;
import org.slf4j.Logger;

import java.lang.reflect.InvocationTargetException;

import static javax.swing.SwingUtilities.invokeAndWait;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Theremin is quite pompous. It is just a module that produces a pitch and a volume.
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.4
 */
public class Theremin extends Module {

	private static final Logger LOGGER = getLogger(Theremin.class);

	public static final String DEFAULT_NAME = "theremin";

	private float pitchSample;
	private float volumeSample;

	private final Output pitch;
	private final Output volume;

	/**
	 * Creates a theremin with default name.
	 *
	 * @see #DEFAULT_NAME
	 * @since 0.0.9
	 */
	public Theremin() {
		this(DEFAULT_NAME);
	}

	/**
	 * @param name name of the theremin
	 */
	public Theremin(String name) {

		super(name);

		pitchSample = 0.0f;
		volumeSample = 0.0f;

		pitch = addPrimaryOutput(name + "_pitch");
		volume = addSecondaryOutput(name + "_volume");

		try {
			invokeAndWait(() -> view = new ThereminView(this));
		} catch (InterruptedException | InvocationTargetException cause) {
			throw new ModuleCreationException(cause);
		}
	}

	@Override
	protected int compute() throws InterruptedException {

		var sampleCount = Settings.INSTANCE.chunkSize();

		var pitchSamples = new float[sampleCount];
		var volumeSamples = new float[sampleCount];

		for (var sampleIndex = 0; sampleIndex < sampleCount; sampleIndex++) {

			pitchSamples[sampleIndex] = pitchSample;
			volumeSamples[sampleIndex] = volumeSample;
		}

		pitch.write(pitchSamples);
		volume.write(volumeSamples);

		return sampleCount;
	}

	/**
	 * @return pitch output of this theremin
	 */
	public Output pitch() {
		return pitch;
	}

	/**
	 * @return volume output of this theremin
	 */
	public Output volume() {
		return volume;
	}

	/**
	 * @param pitch pitch in volts
	 */
	public void setPitch(float pitch) {
		this.pitchSample = pitch;
	}

	/**
	 * @param volumeSample volume in volts
	 */
	public void setVolumeSample(float volumeSample) {
		this.volumeSample = volumeSample;
	}
}
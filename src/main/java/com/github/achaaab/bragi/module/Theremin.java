package com.github.achaaab.bragi.module;

import com.github.achaaab.bragi.common.Settings;
import com.github.achaaab.bragi.common.connection.Output;
import com.github.achaaab.bragi.gui.module.ThereminView;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Jonathan Guéhenneux
 * @since 0.0.4
 */
public class Theremin extends Module {

	private static final Logger LOGGER = getLogger(Theremin.class);

	private float pitchSample;
	private float volumeSample;

	private Output pitch;
	private Output volume;

	/**
	 * @param name name of the theremin
	 */
	public Theremin(String name) {

		super(name);

		pitchSample = 0.0f;
		volumeSample = 0.0f;

		pitch = addPrimaryOutput(name + "_pitch");
		volume = addSecondaryOutput(name + "_volume");

		new ThereminView(this);

		start();
	}

	@Override
	protected int compute() throws InterruptedException {

		var sampleCount = Settings.INSTANCE.getChunkSize();

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
	public Output getPitch() {
		return pitch;
	}

	/**
	 * @return volume output of this theremin
	 */
	public Output getVolume() {
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
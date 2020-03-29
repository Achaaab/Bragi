package com.github.achaaab.bragi.common.wave;

import com.github.achaaab.bragi.common.Settings;

/**
 * @author Jonathan Guéhenneux
 * @since 0.0.2
 */
public abstract class BoundedWaveform implements Waveform {

	protected final String name;

	protected final float minimum;
	protected final float maximum;
	protected final float amplitude;

	/**
	 * @param name name of the waveform to create
	 */
	public BoundedWaveform(String name) {

		this.name = name;

		minimum = Settings.INSTANCE.minimalVoltage();
		maximum = Settings.INSTANCE.maximalVoltage();
		amplitude = maximum - minimum;
	}

	@Override
	public String toString() {
		return name;
	}
}
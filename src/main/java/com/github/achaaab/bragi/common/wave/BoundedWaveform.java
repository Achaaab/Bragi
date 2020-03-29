package com.github.achaaab.bragi.common.wave;

import com.github.achaaab.bragi.common.Settings;

/**
 * @author Jonathan Guéhenneux
 * @since 0.0.2
 */
public abstract class BoundedWaveform implements Waveform {

	protected String name;

	protected float minimum;
	protected float maximum;
	protected float amplitude;

	/**
	 * @param name name of the waveform to create
	 */
	public BoundedWaveform(String name) {

		this.name = name;

		minimum = Settings.INSTANCE.getMinimalVoltage();
		maximum = Settings.INSTANCE.getMaximalVoltage();
		amplitude = maximum - minimum;
	}

	@Override
	public String toString() {
		return name;
	}
}
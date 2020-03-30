package com.github.achaaab.bragi.common.wave;

/**
 * @author Jonathan Guéhenneux
 * @since 0.0.2
 */
public abstract class NamedWaveform implements Waveform {

	protected final String name;

	/**
	 * @param name name of the waveform to create
	 */
	public NamedWaveform(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
}
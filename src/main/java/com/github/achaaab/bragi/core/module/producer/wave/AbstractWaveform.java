package com.github.achaaab.bragi.core.module.producer.wave;

import com.github.achaaab.bragi.common.AbstractNamedEntity;

/**
 * @author Jonathan Guéhenneux
 * @since 0.0.2
 */
public abstract class AbstractWaveform extends AbstractNamedEntity implements Waveform {

	/**
	 * @param name name of the waveform to create
	 * @since 0.2.0
	 */
	public AbstractWaveform(String name) {
		super(name);
	}
}

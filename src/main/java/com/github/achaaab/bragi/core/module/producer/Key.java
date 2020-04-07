package com.github.achaaab.bragi.core.module.producer;

/**
 * TODO add javadoc about this record components
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.6
 */
public record Key(

		String name,
		float voltage,
		int code) {

	@Override
	public String toString() {
		return name;
	}
}
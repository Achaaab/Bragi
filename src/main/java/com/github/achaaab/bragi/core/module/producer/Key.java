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

	/**
	 * @return whether this key corresponds to a C note
	 */
	public boolean c() {
		return name.charAt(0) == 'C';
	}

	/**
	 * @return whether this key corresponds to a sharp note
	 */
	public boolean sharp() {
		return name.charAt(1) == '#';
	}

	/**
	 * @return octave of this key
	 */
	public char octave() {
		return sharp() ? name.charAt(2) : name.charAt(1);
	}
}
package com.github.achaaab.bragi.scale;

/**
 * musical note
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.8
 */
public record Note(
		String name,
		int octave,
		int tone,
		double frequency) {

}
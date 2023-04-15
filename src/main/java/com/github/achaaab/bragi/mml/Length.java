package com.github.achaaab.bragi.mml;

/**
 * Fraction of a whole note.
 *
 * @param fraction
 * @param dotted
 * @author Jonathan Guéhenneux
 * @since 0.1.8
 */
public record Length(

		int fraction,
		boolean dotted) {
}

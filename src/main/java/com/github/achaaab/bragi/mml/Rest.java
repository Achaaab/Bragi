package com.github.achaaab.bragi.mml;

import java.util.List;

/**
 * Command that makes the player rests during a given fraction.
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.8
 */
public class Rest implements Command {

	private final List<Length> lengths;

	/**
	 * @param lengths fractions of a whole note to rest
	 */
	Rest(List<Length> lengths) {
		this.lengths = lengths;
	}

	@Override
	public void execute(Player player) {
		player.rest(lengths);
	}
}
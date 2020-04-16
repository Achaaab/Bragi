package com.github.achaaab.bragi.mml;

/**
 * Command that lowers the current octave by 1.
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.8
 */
public class ShiftDown implements Command {

	/**
	 * @see #SHIFT_DOWN
	 */
	ShiftDown() {

	}

	@Override
	public void execute(Player player) {
		player.shiftDown();
	}
}
package com.github.achaaab.bragi.mml;

/**
 * Command that raises the current octave by 1.
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.8
 */
public class ShiftUp implements Command {

	/**
	 * @see #SHIFT_UP
	 */
	ShiftUp() {

	}

	@Override
	public void execute(Player player) {
		player.shiftUp();
	}
}
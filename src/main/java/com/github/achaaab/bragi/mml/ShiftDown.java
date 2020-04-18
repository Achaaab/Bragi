package com.github.achaaab.bragi.mml;

/**
 * Command that lowers the current octave by 1.
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.8
 */
public class ShiftDown extends AbstractCommand {

	/**
	 * Creates a new command that lowers the octave by 1.
	 */
	ShiftDown() {

	}

	@Override
	public void execute(MmlPlayer player) {
		player.shiftDown();
	}
}
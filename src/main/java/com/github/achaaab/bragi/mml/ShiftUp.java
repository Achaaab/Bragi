package com.github.achaaab.bragi.mml;

/**
 * Command that raises the current octave by 1.
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.8
 */
public class ShiftUp extends AbstractCommand {

	/**
	 * Creates a new command that raises the octave by 1.
	 *
	 * @since 0.2.0
	 */
	ShiftUp() {

	}

	@Override
	public void execute(MmlPlayer player) {
		player.shiftUp();
	}
}

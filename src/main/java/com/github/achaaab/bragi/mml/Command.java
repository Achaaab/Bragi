package com.github.achaaab.bragi.mml;

/**
 * Music Macro Language command
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.8
 */
public interface Command {

	Command END = new End();
	Command SHIFT_UP = new ShiftUp();
	Command SHIFT_DOWN = new ShiftDown();

	/**
	 * Executes this command.
	 *
	 * @param player player on which to execute this command
	 */
	void execute(Player player);
}
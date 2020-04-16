package com.github.achaaab.bragi.mml;

/**
 * end command, meaning that MML parsing is ended
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.8
 */
public class End implements Command {

	/**
	 * @see #END
	 */
	End() {

	}

	@Override
	public void execute(Player player) {
		player.end();
	}
}
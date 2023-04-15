package com.github.achaaab.bragi.mml;

/**
 * end command, meaning that MML parsing is ended
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.8
 */
public class End extends AbstractCommand {

	/**
	 * Creates a new command that ends MML playing.
	 *
	 * @since 0.2.0
	 */
	End() {

	}

	@Override
	public void execute(MmlPlayer player) {
		player.end();
	}
}

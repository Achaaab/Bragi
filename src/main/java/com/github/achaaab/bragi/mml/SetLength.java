package com.github.achaaab.bragi.mml;

/**
 * Command that sets a new note fraction.
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.8
 */
public class SetLength implements Command {

	private final Length length;

	/**
	 * Creates a command that changes the note fraction to {@code value}.
	 *
	 * @param length note fraction to set
	 */
	SetLength(Length length) {
		this.length = length;
	}

	@Override
	public void execute(Player player) {
		player.length(length);
	}
}
package com.github.achaaab.bragi.mml;

/**
 * Command that sets a new note fraction.
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.8
 */
public class SetLength extends AbstractCommand {

	private final Length length;

	/**
	 * Creates a command that changes the note fraction to {@code value}.
	 *
	 * @param length note fraction to set
	 * @since 0.2.0
	 */
	SetLength(Length length) {
		this.length = length;
	}

	@Override
	public void execute(MmlPlayer player) {
		player.length(length);
	}
}

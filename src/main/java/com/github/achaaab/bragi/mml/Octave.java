package com.github.achaaab.bragi.mml;

/**
 * Command that sets a new octave.
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.8
 */
public class Octave extends AbstractCommand {

	private final int value;

	/**
	 * Creates a command that set the current octave to {@code value}.
	 *
	 * @param value octave to set
	 * @since 0.2.0
	 */
	public Octave(int value) {
		this.value = value;
	}

	@Override
	public void execute(MmlPlayer player) {
		player.octave(value);
	}
}

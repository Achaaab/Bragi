package com.github.achaaab.bragi.mml;

/**
 * Command that sets a new octave.
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.8
 */
public class Octave implements Command {

	private final int value;

	/**
	 * Creates a command that set the current octave to {@code value}.
	 *
	 * @param value octave to set
	 */
	public Octave(int value) {
		this.value = value;
	}

	@Override
	public void execute(Player player) {
		player.octave(value);
	}
}
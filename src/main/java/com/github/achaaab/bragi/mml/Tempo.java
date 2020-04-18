package com.github.achaaab.bragi.mml;

/**
 * Command that sets a new tempo.
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.8
 */
public class Tempo extends AbstractCommand {

	private final int value;

	/**
	 * Creates a command that sets the tempo to {@code value}.
	 *
	 * @param value tempo to set in beats per minute
	 */
	public Tempo(int value) {
		this.value = value;
	}

	@Override
	public void execute(MmlPlayer player) {
		player.tempo(value);
	}
}
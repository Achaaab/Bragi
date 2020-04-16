package com.github.achaaab.bragi.mml;

/**
 * Command that sets the player volume.
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.8
 */
public class Volume implements Command {

	private final int value;

	/**
	 * @param value volume value
	 */
	public Volume(int value) {
		this.value = value;
	}

	@Override
	public void execute(Player player) {
		player.volume(value);
	}
}
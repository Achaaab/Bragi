package com.github.achaaab.bragi.mml;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Command that makes a player play a given note of a given fraction.
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.8
 */
public class Play extends AbstractCommand {

	private static final Map<String, Integer> TONES;

	static {

		TONES = new HashMap<>();

		TONES.put("C-", -1);
		TONES.put("C", 0);

		TONES.put("C+", 1);
		TONES.put("C#", 1);
		TONES.put("D-", 1);

		TONES.put("D", 2);

		TONES.put("D+", 3);
		TONES.put("D#", 3);
		TONES.put("E-", 3);

		TONES.put("E", 4);

		TONES.put("F", 5);

		TONES.put("F+", 6);
		TONES.put("F#", 6);
		TONES.put("G-", 6);

		TONES.put("G", 7);

		TONES.put("G+", 8);
		TONES.put("G#", 8);
		TONES.put("A-", 8);

		TONES.put("A", 9);

		TONES.put("A+", 10);
		TONES.put("A#", 10);
		TONES.put("B-", 10);

		TONES.put("B", 11);
		TONES.put("B+", 12);
	}

	private final int tone;
	private final List<Length> lengths;

	/**
	 * @param note      note to play
	 * @param lengths fractions of a whole note to play
	 */
	public Play(String note, List<Length> lengths) {

		this.lengths = lengths;

		tone = TONES.get(note);
	}

	@Override
	public void execute(MmlPlayer player) {
		player.play(tone, lengths);
	}
}
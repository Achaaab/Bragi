package com.github.achaaab.bragi.scale;

import static java.lang.Math.pow;

/**
 * chromatic scale with C tonic
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.8
 */
public class ChromaticScale implements Scale {

	public static final int BASE_OCTAVE = 4;
	public static final int BASE_TONE = 9;
	public static final double BASE_FREQUENCY = 440;

	private static final int TONE_COUNT = 12;

	private static final boolean[] SHARP_TONES =
			{ false, true, false, true, false, false, true, false, true, false, true, false };

	private static final String[] NAMES = { "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B" };

	/**
	 * @param note note
	 * @return whether the note is a sharp note in chromatic scale
	 * @since 0.2.0
	 */
	public static boolean sharp(Note note) {
		return SHARP_TONES[note.tone()];
	}

	@Override
	public String name(Note note) {

		var octave = note.octave();
		var tone = note.tone();

		return NAMES[tone] + octave;
	}

	@Override
	public double frequency(Note note) {

		var octave = note.octave();
		var tone = note.tone();

		double interval = TONE_COUNT * (octave - BASE_OCTAVE) + tone - BASE_TONE;

		return BASE_FREQUENCY * pow(2, interval / TONE_COUNT);
	}

	@Override
	public int toneCount() {
		return TONE_COUNT;
	}
}

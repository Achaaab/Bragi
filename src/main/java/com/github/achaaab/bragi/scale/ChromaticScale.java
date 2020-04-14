package com.github.achaaab.bragi.scale;

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
	 */
	public static boolean isSharp(Note note) {
		return SHARP_TONES[note.tone()];
	}

	/**
	 * @param octave octave of the note
	 * @param tone   tone of the note
	 * @return name of the note
	 */
	private static String getName(int octave, int tone) {
		return NAMES[tone] + octave;
	}

	/**
	 * @param octave octave
	 * @param tone   tone
	 * @return frequency for the given octave and tone, in hertz (Hz)
	 */
	private static double getFrequency(int octave, int tone) {

		double interval = TONE_COUNT * (octave - BASE_OCTAVE) + tone - BASE_TONE;

		return BASE_FREQUENCY * Math.pow(2, interval / TONE_COUNT);
	}

	@Override
	public int toneCount() {
		return TONE_COUNT;
	}

	@Override
	public Note note(int octave, int tone) {
		return new Note(getName(octave, tone), octave, tone, getFrequency(octave, tone));
	}
}
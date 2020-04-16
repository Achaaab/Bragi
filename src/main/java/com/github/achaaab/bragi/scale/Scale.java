package com.github.achaaab.bragi.scale;

/**
 * A scale is a set of musical notes ordered by frequency.
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.8
 */
public interface Scale {

	/**
	 * @return number of tones in this scale
	 */
	int toneCount();

	/**
	 * @param note musical note
	 * @return unique name of the given note
	 */
	String name(Note note);

	/**
	 * @param note musical note
	 * @return frequency of the given note in hertz (Hz)
	 */
	double frequency(Note note);

	/**
	 * If the given note is the last note of its octave, the following note will be the first note of the
	 * following octave.
	 *
	 * @param note note
	 * @return note that follows the given note in this scale
	 */
	default Note followingNote(Note note) {

		var octave = note.octave();
		var tone = note.tone();

		return tone + 1 == toneCount() ?
				new Note(octave + 1, 0) :
				new Note(octave, tone + 1);
	}
}
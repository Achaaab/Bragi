package com.github.achaaab.bragi.mml;

import com.github.achaaab.bragi.core.module.producer.Keyboard;
import com.github.achaaab.bragi.scale.Note;
import org.slf4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static java.lang.Thread.sleep;
import static javax.swing.SwingUtilities.invokeAndWait;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Music Macro Language player
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.8
 */
public class MmlPlayer {

	private static final Logger LOGGER = getLogger(MmlPlayer.class);

	public static final int DEFAULT_TEMPO = 120;
	public static final int DEFAULT_OCTAVE = 4;
	public static final int DEFAULT_LENGTH = 4;

	private final Keyboard keyboard;

	private int tempo;
	private Length length;
	private int octave;

	private boolean end;

	private MmlPlayerView view;

	/**
	 * @param keyboard keyboard on which to play
	 * @since 0.2.0
	 */
	public MmlPlayer(Keyboard keyboard) {

		this.keyboard = keyboard;

		try {
			invokeAndWait(() -> view = new MmlPlayerView(this));
		} catch (InterruptedException | InvocationTargetException cause) {
			throw new MmlException(cause);
		}
	}

	/**
	 * @return view of this MML player
	 * @since 0.2.0
	 */
	public MmlPlayerView view() {
		return view;
	}

	/**
	 * @param mml MML partition
	 * @since 0.2.0
	 */
	public void play(String mml) {

		start();
		var parser = new Parser(mml);

		while (!end) {

			var command = parser.nextCommand();
			view.showCurrentCommand(command);
			command.execute(this);
		}
	}

	/**
	 * Plays a note.
	 *
	 * @param tone tone of the note to play
	 * @param lengths fractions of a whole note to play
	 * @since 0.2.0
	 */
	void play(int tone, List<Length> lengths) {

		var noteTone = tone;
		var noteOctave = octave;

		if (noteTone < 0) {

			noteTone += 12;
			noteOctave--;

		} else if (noteTone > 11) {

			noteTone -= 12;
			noteOctave++;
		}

		var note = new Note(noteOctave, noteTone);

		keyboard.play(note);
		wait(lengths);
		keyboard.stop(note);
	}

	/**
	 * Rests.
	 *
	 * @param lengths fractions of a whole note to rest
	 * @since 0.2.0
	 */
	void rest(List<Length> lengths) {
		wait(lengths);
	}

	/**
	 * Sets default tempo, default fraction and default octave.
	 *
	 * @since 0.2.0
	 */
	void start() {

		end = false;

		tempo = DEFAULT_TEMPO;
		length = new Length(DEFAULT_LENGTH, false);
		octave = DEFAULT_OCTAVE;
	}

	/**
	 * Ends this MML.
	 *
	 * @since 0.2.0
	 */
	void end() {
		end = true;
	}

	/**
	 * @param tempo tempo to set in beats per minute
	 * @since 0.2.0
	 */
	void tempo(int tempo) {
		this.tempo = tempo;
	}

	/**
	 * @param octave new octave to set
	 * @since 0.2.0
	 */
	void octave(int octave) {
		this.octave = octave;
	}

	/**
	 * @param length fraction as a fraction of a whole note (1 for a whole note, 2 for a half note,
	 * 4 for a quarter note...)
	 * @since 0.2.0
	 */
	void length(Length length) {
		this.length = length;
	}

	/**
	 * Lowers the current octave by 1.
	 *
	 * @since 0.2.0
	 */
	void shiftUp() {
		octave++;
	}

	/**
	 * Raises the current octave by 1.
	 *
	 * @since 0.2.0
	 */
	void shiftDown() {
		octave--;
	}

	/**
	 * Sets the player volume.
	 *
	 * @param volume volume to set
	 * @since 0.2.0
	 */
	void volume(int volume) {

	}

	/**
	 * Waits the end of the current note or rest.
	 *
	 * @param lengths fractions of a whole note to wait
	 * @since 0.2.0
	 */
	private void wait(List<Length> lengths) {

		var duration = lengths.stream().mapToInt(this::duration).sum();

		try {
			sleep(duration);
		} catch (InterruptedException cause) {
			throw new MmlException(cause);
		}
	}

	/**
	 * @param length fraction of a whole note
	 * @return duration corresponding duration in milliseconds (ms)
	 * @since 0.2.0
	 */
	private int duration(Length length) {

		var fraction = length.fraction();

		if (fraction == 0) {
			fraction = this.length.fraction();
		}

		return 240_000 / tempo / fraction;
	}
}

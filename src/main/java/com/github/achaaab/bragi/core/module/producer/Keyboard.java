package com.github.achaaab.bragi.core.module.producer;

import com.github.achaaab.bragi.common.Settings;
import com.github.achaaab.bragi.core.connection.Output;
import com.github.achaaab.bragi.core.module.Module;
import com.github.achaaab.bragi.core.module.ModuleCreationException;
import com.github.achaaab.bragi.gui.module.KeyboardView;
import com.github.achaaab.bragi.mml.MmlPlayer;
import com.github.achaaab.bragi.scale.ChromaticScale;
import com.github.achaaab.bragi.scale.Note;
import com.github.achaaab.bragi.scale.Scale;
import org.slf4j.Logger;

import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.achaaab.bragi.scale.ChromaticScale.BASE_FREQUENCY;
import static com.github.achaaab.bragi.scale.ChromaticScale.sharp;
import static java.awt.event.KeyEvent.getExtendedKeyCodeForChar;
import static java.lang.Math.log;
import static javax.swing.SwingUtilities.invokeAndWait;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Jonathan Guéhenneux
 * @since 0.0.6
 */
public class Keyboard extends Module {

	private static final Logger LOGGER = getLogger(Keyboard.class);

	public static final String DEFAULT_NAME = "keyboard";

	private static final double VOLTS_PER_OCTAVE = 1.0;
	private static final double LOG_2 = log(2);

	private static final int[] KEY_CODES = {

			// third octave (from F3 to B3)
			KeyEvent.VK_TAB,
			getExtendedKeyCodeForChar('&'),
			KeyEvent.VK_A,
			getExtendedKeyCodeForChar('é'),
			KeyEvent.VK_Z,
			getExtendedKeyCodeForChar('"'),
			KeyEvent.VK_E,

			// fourth octave (from C4 to B4)
			KeyEvent.VK_R,
			getExtendedKeyCodeForChar('('),
			KeyEvent.VK_T,
			getExtendedKeyCodeForChar('-'),
			KeyEvent.VK_Y,
			KeyEvent.VK_U,
			getExtendedKeyCodeForChar('_'),
			KeyEvent.VK_I,
			getExtendedKeyCodeForChar('ç'),
			KeyEvent.VK_O,
			getExtendedKeyCodeForChar('à'),
			KeyEvent.VK_P,

			// fifth octave (from C5 to B5)
			KeyEvent.VK_LESS,
			KeyEvent.VK_Q,
			KeyEvent.VK_W,
			KeyEvent.VK_S,
			KeyEvent.VK_X,
			KeyEvent.VK_C,
			KeyEvent.VK_F,
			KeyEvent.VK_V,
			KeyEvent.VK_G,
			KeyEvent.VK_B,
			KeyEvent.VK_H,
			KeyEvent.VK_N,

			// sixth octave (from C6 to E6)
			KeyEvent.VK_COMMA,
			KeyEvent.VK_K,
			KeyEvent.VK_SEMICOLON,
			KeyEvent.VK_L,
			KeyEvent.VK_COLON
	};

	private final Output output;
	private final Output gate;

	private final MmlPlayer mmlPlayer;
	private final Scale scale;
	private final List<Key> keys;
	private final Map<Note, Key> noteKeys;

	private int pressedKeyCount;
	private int previousPressedKeyCount;
	private float voltage;

	/**
	 * Creates a keyboard with default name.
	 *
	 * @see #DEFAULT_NAME
	 * @since 0.0.9
	 */
	public Keyboard() {
		this(DEFAULT_NAME);
	}

	/**
	 * @param name keyboard name
	 */
	public Keyboard(String name) {

		super(name);

		output = addPrimaryOutput(name + "_output");
		gate = addSecondaryOutput(name + "_gate");

		mmlPlayer = new MmlPlayer(this);
		scale = new ChromaticScale();
		keys = new ArrayList<>();
		noteKeys = new HashMap<>();

		var note = new Note(3, 5);

		for (int code : KEY_CODES) {

			var key = createKey(note, code);
			keys.add(key);
			noteKeys.put(note, key);
			note = scale.followingNote(note);
		}

		previousPressedKeyCount = 0;
		pressedKeyCount = 0;

		try {
			invokeAndWait(() -> view = new KeyboardView(this));
		} catch (InterruptedException | InvocationTargetException cause) {
			throw new ModuleCreationException(cause);
		}
	}

	@Override
	public int compute() throws InterruptedException {

		var sampleCount = Settings.INSTANCE.chunkSize();

		var samples = new float[sampleCount];

		for (var sampleIndex = 0; sampleIndex < sampleCount; sampleIndex++) {
			samples[sampleIndex] = voltage;
		}

		output.write(samples);

		var gateSample = 0.0f;

		if (pressedKeyCount > previousPressedKeyCount) {
			gateSample = 1.0f;
		} else if (pressedKeyCount == 0 && previousPressedKeyCount > 0) {
			gateSample = -1.0f;
		}

		previousPressedKeyCount = pressedKeyCount;

		gate.write(new float[] { gateSample });

		return sampleCount;
	}

	/**
	 * @param note note
	 * @return corresponding voltage
	 */
	private float voltage(Note note) {

		var frequency = scale.frequency(note);
		return (float) (VOLTS_PER_OCTAVE * log(frequency / BASE_FREQUENCY) / LOG_2);
	}

	/**
	 * @param note key note
	 * @param code key code
	 * @return created key
	 */
	private Key createKey(Note note, int code) {

		var name = scale.name(note);
		var sharp = sharp(note);
		var voltage = voltage(note);

		return new Key(this, note, name, sharp, voltage, code);
	}

	/**
	 * Play a note. If the given note is associated to a key, set the key as pressed.
	 *
	 * @param note note to play
	 */
	public synchronized void play(Note note) {

		var key = noteKeys.get(note);

		if (key == null) {

			voltage = voltage(note);
			pressedKeyCount++;

		} else {

			press(key);
		}
	}

	/**
	 * Stop playing a note. If the given note is associated to a key, set the key as released.
	 *
	 * @param note note to stop playing
	 */
	public synchronized void stop(Note note) {

		var key = noteKeys.get(note);

		if (key == null) {
			pressedKeyCount--;
		} else {
			release(key);
		}
	}

	/**
	 * Presses a key.
	 *
	 * @param key key to press
	 */
	public synchronized void press(Key key) {

		voltage = key.voltage();
		pressedKeyCount++;

		key.setPressed(true);
	}

	/**
	 * Releases a key.
	 *
	 * @param key key to release
	 */
	public synchronized void release(Key key) {

		pressedKeyCount--;

		key.setPressed(false);
	}

	/**
	 * @return MML player
	 */
	public MmlPlayer mmlPlayer() {
		return mmlPlayer;
	}

	/**
	 * @return keys keys
	 */
	public List<Key> keys() {
		return keys;
	}

	/**
	 * @return keyboard gate
	 */
	public Output gate() {
		return gate;
	}
}
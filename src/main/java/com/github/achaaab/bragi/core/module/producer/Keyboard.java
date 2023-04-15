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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.achaaab.bragi.scale.ChromaticScale.BASE_FREQUENCY;
import static com.github.achaaab.bragi.scale.ChromaticScale.sharp;
import static java.awt.event.KeyEvent.VK_A;
import static java.awt.event.KeyEvent.VK_B;
import static java.awt.event.KeyEvent.VK_C;
import static java.awt.event.KeyEvent.VK_COLON;
import static java.awt.event.KeyEvent.VK_COMMA;
import static java.awt.event.KeyEvent.VK_E;
import static java.awt.event.KeyEvent.VK_F;
import static java.awt.event.KeyEvent.VK_G;
import static java.awt.event.KeyEvent.VK_H;
import static java.awt.event.KeyEvent.VK_I;
import static java.awt.event.KeyEvent.VK_K;
import static java.awt.event.KeyEvent.VK_L;
import static java.awt.event.KeyEvent.VK_LESS;
import static java.awt.event.KeyEvent.VK_N;
import static java.awt.event.KeyEvent.VK_O;
import static java.awt.event.KeyEvent.VK_P;
import static java.awt.event.KeyEvent.VK_Q;
import static java.awt.event.KeyEvent.VK_R;
import static java.awt.event.KeyEvent.VK_S;
import static java.awt.event.KeyEvent.VK_SEMICOLON;
import static java.awt.event.KeyEvent.VK_T;
import static java.awt.event.KeyEvent.VK_TAB;
import static java.awt.event.KeyEvent.VK_U;
import static java.awt.event.KeyEvent.VK_V;
import static java.awt.event.KeyEvent.VK_W;
import static java.awt.event.KeyEvent.VK_X;
import static java.awt.event.KeyEvent.VK_Y;
import static java.awt.event.KeyEvent.VK_Z;
import static java.awt.event.KeyEvent.getExtendedKeyCodeForChar;
import static java.lang.Math.log;
import static java.util.Arrays.fill;
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
			VK_TAB,
			getExtendedKeyCodeForChar('&'),
			VK_A,
			getExtendedKeyCodeForChar('é'),
			VK_Z,
			getExtendedKeyCodeForChar('"'),
			VK_E,

			// fourth octave (from C4 to B4)
			VK_R,
			getExtendedKeyCodeForChar('('),
			VK_T,
			getExtendedKeyCodeForChar('-'),
			VK_Y,
			VK_U,
			getExtendedKeyCodeForChar('_'),
			VK_I,
			getExtendedKeyCodeForChar('ç'),
			VK_O,
			getExtendedKeyCodeForChar('à'),
			VK_P,

			// fifth octave (from C5 to B5)
			VK_LESS, VK_Q, VK_W, VK_S, VK_X, VK_C, VK_F, VK_V, VK_G, VK_B, VK_H, VK_N,

			// sixth octave (from C6 to E6)
			VK_COMMA, VK_K, VK_SEMICOLON, VK_L, VK_COLON };

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
	 * @since 0.2.0
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
		fill(samples, voltage);

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
	 * @since 0.2.0
	 */
	private float voltage(Note note) {

		var frequency = scale.frequency(note);
		return (float) (VOLTS_PER_OCTAVE * log(frequency / BASE_FREQUENCY) / LOG_2);
	}

	/**
	 * @param note key note
	 * @param code key code
	 * @return created key
	 * @since 0.2.0
	 */
	private Key createKey(Note note, int code) {

		var name = scale.name(note);
		var sharp = sharp(note);
		var voltage = voltage(note);

		return new Key(this, note, name, sharp, voltage, code);
	}

	/**
	 * Plays a note. If the given note is associated to a key, set the key as pressed.
	 *
	 * @param note note to play
	 * @since 0.2.0
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
	 * Stops playing a note. If the given note is associated to a key, set the key as released.
	 *
	 * @param note note to stop playing
	 * @since 0.2.0
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
	 * @since 0.2.0
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
	 * @since 0.2.0
	 */
	public synchronized void release(Key key) {

		pressedKeyCount--;

		key.setPressed(false);
	}

	/**
	 * @return MML player
	 * @since 0.2.0
	 */
	public MmlPlayer mmlPlayer() {
		return mmlPlayer;
	}

	/**
	 * @return keys
	 * @since 0.2.0
	 */
	public List<Key> keys() {
		return keys;
	}

	/**
	 * @return keyboard gate
	 * @since 0.2.0
	 */
	public Output gate() {
		return gate;
	}
}

package com.github.achaaab.bragi.core.module.producer;

import com.github.achaaab.bragi.common.Settings;
import com.github.achaaab.bragi.core.connection.Output;
import com.github.achaaab.bragi.core.module.Module;
import com.github.achaaab.bragi.core.module.ModuleCreationException;
import com.github.achaaab.bragi.gui.module.KeyboardView;
import com.github.achaaab.bragi.scale.ChromaticScale;
import com.github.achaaab.bragi.scale.Note;
import com.github.achaaab.bragi.scale.Scale;
import org.slf4j.Logger;

import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static com.github.achaaab.bragi.scale.ChromaticScale.BASE_FREQUENCY;
import static com.github.achaaab.bragi.scale.ChromaticScale.isSharp;
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

	private static final float VOLTS_PER_OCTAVE = 1.0f;
	private static final double LOG_2 = log(2);

	/**
	 * @param note key note
	 * @param code key code
	 * @return created key
	 */
	private static Key createKey(Note note, int code) {

		var sharp = isSharp(note);
		var frequency = note.frequency();
		var voltage = (float) (log(frequency / BASE_FREQUENCY) / LOG_2);

		return new Key(note, sharp, voltage, code);
	}

	private final Output output;
	private final Output gate;

	private final Scale scale;
	private final List<Key> keys;

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

		scale = new ChromaticScale();

		var note = scale.note(3, 5);

		keys = new ArrayList<>();

		keys.add(createKey(note, KeyEvent.VK_TAB));
		keys.add(createKey(note = scale.followingNote(note), getExtendedKeyCodeForChar('&')));
		keys.add(createKey(note = scale.followingNote(note), KeyEvent.VK_A));
		keys.add(createKey(note = scale.followingNote(note), getExtendedKeyCodeForChar('é')));
		keys.add(createKey(note = scale.followingNote(note), KeyEvent.VK_Z));
		keys.add(createKey(note = scale.followingNote(note), getExtendedKeyCodeForChar('"')));
		keys.add(createKey(note = scale.followingNote(note), KeyEvent.VK_E));
		keys.add(createKey(note = scale.followingNote(note), KeyEvent.VK_R));
		keys.add(createKey(note = scale.followingNote(note), getExtendedKeyCodeForChar('(')));
		keys.add(createKey(note = scale.followingNote(note), KeyEvent.VK_T));
		keys.add(createKey(note = scale.followingNote(note), getExtendedKeyCodeForChar('-')));
		keys.add(createKey(note = scale.followingNote(note), KeyEvent.VK_Y));
		keys.add(createKey(note = scale.followingNote(note), KeyEvent.VK_U));
		keys.add(createKey(note = scale.followingNote(note), getExtendedKeyCodeForChar('_')));
		keys.add(createKey(note = scale.followingNote(note), KeyEvent.VK_I));
		keys.add(createKey(note = scale.followingNote(note), getExtendedKeyCodeForChar('ç')));
		keys.add(createKey(note = scale.followingNote(note), KeyEvent.VK_O));
		keys.add(createKey(note = scale.followingNote(note), getExtendedKeyCodeForChar('à')));
		keys.add(createKey(note = scale.followingNote(note), KeyEvent.VK_P));
		keys.add(createKey(note = scale.followingNote(note), KeyEvent.VK_LESS));
		keys.add(createKey(note = scale.followingNote(note), KeyEvent.VK_Q));
		keys.add(createKey(note = scale.followingNote(note), KeyEvent.VK_W));
		keys.add(createKey(note = scale.followingNote(note), KeyEvent.VK_S));
		keys.add(createKey(note = scale.followingNote(note), KeyEvent.VK_X));
		keys.add(createKey(note = scale.followingNote(note), KeyEvent.VK_C));
		keys.add(createKey(note = scale.followingNote(note), KeyEvent.VK_F));
		keys.add(createKey(note = scale.followingNote(note), KeyEvent.VK_V));
		keys.add(createKey(note = scale.followingNote(note), KeyEvent.VK_G));
		keys.add(createKey(note = scale.followingNote(note), KeyEvent.VK_B));
		keys.add(createKey(note = scale.followingNote(note), KeyEvent.VK_H));
		keys.add(createKey(note = scale.followingNote(note), KeyEvent.VK_N));
		keys.add(createKey(note = scale.followingNote(note), KeyEvent.VK_COMMA));
		keys.add(createKey(note = scale.followingNote(note), KeyEvent.VK_K));
		keys.add(createKey(note = scale.followingNote(note), KeyEvent.VK_SEMICOLON));
		keys.add(createKey(note = scale.followingNote(note), KeyEvent.VK_L));
		keys.add(createKey(scale.followingNote(note), KeyEvent.VK_COLON));

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
	 * @param voltage voltage output
	 */
	public synchronized void press(float voltage) {

		this.voltage = voltage;

		pressedKeyCount++;
	}

	/**
	 * release a key
	 */
	public synchronized void release() {
		pressedKeyCount--;
	}

	/**
	 * @return keys keys
	 */
	public List<Key> getKeys() {
		return keys;
	}

	/**
	 * @return keyboard gate
	 */
	public Output gate() {
		return gate;
	}
}
package fr.guehenneux.bragi.module;

import fr.guehenneux.bragi.common.Key;
import fr.guehenneux.bragi.common.Settings;
import fr.guehenneux.bragi.common.connection.Output;
import fr.guehenneux.bragi.gui.module.KeyboardView;
import org.slf4j.Logger;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import static java.awt.event.KeyEvent.getExtendedKeyCodeForChar;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Keyboard module
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.6
 */
public class Keyboard extends Module {

	private static final float VOLTS_PER_OCTAVE = 1.0f;

	private static final Logger LOGGER = getLogger(Keyboard.class);

	private Output output;
	private Output gate;

	private float voltage;
	private List<Key> keys;
	private float gateSample;
	private int pressedKeyCount;

	/**
	 * @param name keyboard name
	 */
	public Keyboard(String name) {

		super(name);

		output = addPrimaryOutput(name + "_output");
		gate = addSecondaryOutput(name + "_gate");

		keys = new ArrayList<>();

		keys.add(new Key("F3", -16 * VOLTS_PER_OCTAVE / 12, KeyEvent.VK_TAB));
		keys.add(new Key("F#3", -15 * VOLTS_PER_OCTAVE / 12, getExtendedKeyCodeForChar('&')));
		keys.add(new Key("G3", -14 * VOLTS_PER_OCTAVE / 12, KeyEvent.VK_A));
		keys.add(new Key("G#3", -13 * VOLTS_PER_OCTAVE / 12, getExtendedKeyCodeForChar('é')));
		keys.add(new Key("A3", -12 * VOLTS_PER_OCTAVE / 12, KeyEvent.VK_Z));
		keys.add(new Key("A#3", -11 * VOLTS_PER_OCTAVE / 12, getExtendedKeyCodeForChar('"')));
		keys.add(new Key("B3", -10 * VOLTS_PER_OCTAVE / 12, KeyEvent.VK_E));

		keys.add(new Key("C4", -9 * VOLTS_PER_OCTAVE / 12, KeyEvent.VK_R));
		keys.add(new Key("C#4", -8 * VOLTS_PER_OCTAVE / 12, getExtendedKeyCodeForChar('(')));
		keys.add(new Key("D4", -7 * VOLTS_PER_OCTAVE / 12, KeyEvent.VK_T));
		keys.add(new Key("D#4", -6 * VOLTS_PER_OCTAVE / 12, getExtendedKeyCodeForChar('-')));
		keys.add(new Key("E4", -5 * VOLTS_PER_OCTAVE / 12, KeyEvent.VK_Y));
		keys.add(new Key("F4", -4 * VOLTS_PER_OCTAVE / 12, KeyEvent.VK_U));
		keys.add(new Key("F#4", -3 * VOLTS_PER_OCTAVE / 12, getExtendedKeyCodeForChar('_')));
		keys.add(new Key("G4", -2 * VOLTS_PER_OCTAVE / 12, KeyEvent.VK_I));
		keys.add(new Key("G#4", -1 * VOLTS_PER_OCTAVE / 12, getExtendedKeyCodeForChar('ç')));
		keys.add(new Key("A4", 0 * VOLTS_PER_OCTAVE / 12, KeyEvent.VK_O));
		keys.add(new Key("A#4", 1 * VOLTS_PER_OCTAVE / 12, getExtendedKeyCodeForChar('à')));
		keys.add(new Key("B4", 2 * VOLTS_PER_OCTAVE / 12, KeyEvent.VK_P));

		keys.add(new Key("C5", 3 * VOLTS_PER_OCTAVE / 12, KeyEvent.VK_LESS));
		keys.add(new Key("C#5", 4 * VOLTS_PER_OCTAVE / 12, KeyEvent.VK_Q));
		keys.add(new Key("D5", 5 * VOLTS_PER_OCTAVE / 12, KeyEvent.VK_W));
		keys.add(new Key("D#5", 6 * VOLTS_PER_OCTAVE / 12, KeyEvent.VK_S));
		keys.add(new Key("E5", 7 * VOLTS_PER_OCTAVE / 12, KeyEvent.VK_X));
		keys.add(new Key("F5", 8 * VOLTS_PER_OCTAVE / 12, KeyEvent.VK_C));
		keys.add(new Key("F#5", 9 * VOLTS_PER_OCTAVE / 12, KeyEvent.VK_F));
		keys.add(new Key("G5", 10 * VOLTS_PER_OCTAVE / 12, KeyEvent.VK_V));
		keys.add(new Key("G#5", 11 * VOLTS_PER_OCTAVE / 12, KeyEvent.VK_G));
		keys.add(new Key("A5", 12 * VOLTS_PER_OCTAVE / 12, KeyEvent.VK_B));
		keys.add(new Key("A#5", 13 * VOLTS_PER_OCTAVE / 12, KeyEvent.VK_H));
		keys.add(new Key("B5", 14 * VOLTS_PER_OCTAVE / 12, KeyEvent.VK_N));

		keys.add(new Key("C6", 15 * VOLTS_PER_OCTAVE / 12, KeyEvent.VK_COMMA));
		keys.add(new Key("C#6", 16 * VOLTS_PER_OCTAVE / 12, KeyEvent.VK_K));
		keys.add(new Key("D6", 17 * VOLTS_PER_OCTAVE / 12, KeyEvent.VK_SEMICOLON));
		keys.add(new Key("D#6", 18 * VOLTS_PER_OCTAVE / 12, KeyEvent.VK_L));
		keys.add(new Key("E6", 19 * VOLTS_PER_OCTAVE / 12, KeyEvent.VK_COLON));

		gateSample = 0.0f;
		pressedKeyCount = 0;

		new KeyboardView(this);

		start();
	}

	@Override
	public int compute() throws InterruptedException {

		var sampleCount = Settings.INSTANCE.getChunkSize();

		var samples = new float[sampleCount];

		for (var sampleIndex = 0; sampleIndex < sampleCount; sampleIndex++) {
			samples[sampleIndex] = voltage;
		}

		output.write(samples);

		gate.write(new float[] { gateSample });
		gateSample = 0.0f;

		return sampleCount;
	}

	/**
	 * @param voltage voltage output
	 */
	public void press(float voltage) {

		this.voltage = voltage;

		pressedKeyCount++;
		gateSample = 1.0f;
	}

	/**
	 * release a key
	 */
	public void release() {

		pressedKeyCount--;

		if (pressedKeyCount == 0) {
			gateSample = -1.0f;
		}
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
	public Output getGate() {
		return gate;
	}
}
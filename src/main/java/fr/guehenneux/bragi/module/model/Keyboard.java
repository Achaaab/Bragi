package fr.guehenneux.bragi.module.model;

import fr.guehenneux.bragi.Settings;
import fr.guehenneux.bragi.connection.Input;
import fr.guehenneux.bragi.connection.Output;
import fr.guehenneux.bragi.module.view.KeyboardView;
import fr.guehenneux.bragi.wave.Sawtooth;
import fr.guehenneux.bragi.wave.Triangle;
import fr.guehenneux.bragi.wave.Wave;
import fr.guehenneux.bragi.wave.Waveform;
import org.slf4j.Logger;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Jonathan Guéhenneux
 */
public class Keyboard extends Module {

	private static final Logger LOGGER = getLogger(Keyboard.class);

	private Input modulation;
	private Output output;
	private Output gate;

	private List<Key> keys;
	private Wave wave;
	private float gateSample;
	private int pressedKeyCount;

	/**
	 * @param name keyboard name
	 */
	public Keyboard(String name) {

		super(name);

		modulation = addSecondaryInput(name + "_modulation");
		output = addPrimaryOutput(name + "_output");
		gate = addSecondaryOutput(name + "_gate");

		wave = new Wave(Triangle.INSTANCE, 440);

		keys = new ArrayList<>();

		keys.add(new Key("F3", 174.6141, KeyEvent.VK_TAB));
		keys.add(new Key("F#3", 184.9972, KeyEvent.getExtendedKeyCodeForChar('&')));
		keys.add(new Key("G3", 195.9977, KeyEvent.VK_A));
		keys.add(new Key("G#3", 207.6523, KeyEvent.getExtendedKeyCodeForChar('é')));
		keys.add(new Key("A3", 220.0000, KeyEvent.VK_Z));
		keys.add(new Key("A#3", 233.0819, KeyEvent.getExtendedKeyCodeForChar('"')));
		keys.add(new Key("B3", 246.9417, KeyEvent.VK_E));

		keys.add(new Key("C4", 261.6256, KeyEvent.VK_R));
		keys.add(new Key("C#4", 277.1826, KeyEvent.getExtendedKeyCodeForChar('(')));
		keys.add(new Key("D4", 293.6648, KeyEvent.VK_T));
		keys.add(new Key("D#4", 311.1270, KeyEvent.getExtendedKeyCodeForChar('-')));
		keys.add(new Key("E4", 329.6276, KeyEvent.VK_Y));
		keys.add(new Key("F4", 349.2282, KeyEvent.VK_U));
		keys.add(new Key("F#4", 369.9944, KeyEvent.getExtendedKeyCodeForChar('_')));
		keys.add(new Key("G4", 391.9954, KeyEvent.VK_I));
		keys.add(new Key("G#4", 415.3047, KeyEvent.getExtendedKeyCodeForChar('ç')));
		keys.add(new Key("A4", 440.0000, KeyEvent.VK_O));
		keys.add(new Key("A#4", 466.1638, KeyEvent.getExtendedKeyCodeForChar('à')));
		keys.add(new Key("B4", 493.8833, KeyEvent.VK_P));

		keys.add(new Key("C5", 523.2511, KeyEvent.VK_LESS));
		keys.add(new Key("C#5", 554.3653, KeyEvent.VK_Q));
		keys.add(new Key("D5", 587.3295, KeyEvent.VK_W));
		keys.add(new Key("D#5", 622.2540, KeyEvent.VK_S));
		keys.add(new Key("E5", 659.2551, KeyEvent.VK_X));
		keys.add(new Key("F5", 698.4565, KeyEvent.VK_C));
		keys.add(new Key("F#5", 739.9888, KeyEvent.VK_F));
		keys.add(new Key("G5", 783.9909, KeyEvent.VK_V));
		keys.add(new Key("G#5", 830.6094, KeyEvent.VK_G));
		keys.add(new Key("A5", 880.0000, KeyEvent.VK_B));
		keys.add(new Key("A#5", 932.3275, KeyEvent.VK_H));
		keys.add(new Key("B5", 987.7666, KeyEvent.VK_N));

		keys.add(new Key("C6", 1046.502, KeyEvent.VK_COMMA));
		keys.add(new Key("C#6", 1108.731, KeyEvent.VK_K));
		keys.add(new Key("D6", 1174.659, KeyEvent.VK_SEMICOLON));
		keys.add(new Key("D#6", 1244.508, KeyEvent.VK_L));
		keys.add(new Key("E6", 1318.510, KeyEvent.VK_COLON));

		gateSample = 0.0f;
		pressedKeyCount = 0;

		new KeyboardView(this);

		start();
	}

	@Override
	public int compute() throws InterruptedException {

		var sampleCount = Settings.INSTANCE.getChunkSize();
		var sampleLength = Settings.INSTANCE.getFrameLength();

		var modulationSamples = modulation.read();

		var samples = wave.getSamples(0, modulationSamples, sampleCount, sampleLength);
		var gateSamples = new float[]{gateSample};

		output.write(samples);
		gate.write(gateSamples);

		return sampleCount;
	}

	/**
	 * @return keyboard frequency
	 */
	public double getFrequency() {
		return wave.getFrequency();
	}

	/**
	 * @param frequency key frequency
	 */
	public void press(double frequency) {

		wave.setFrequency(frequency);
		pressedKeyCount++;
		gateSample = 1.0f;
	}

	/**
	 *
	 */
	public void release() {

		pressedKeyCount--;

		if (pressedKeyCount == 0) {
			gateSample = -1.0f;
		}
	}

	/**
	 * @return current waveform
	 */
	public Waveform getWaveform() {
		return wave.getWaveform();
	}

	/**
	 * @param waveform waveform to set
	 */
	public void setWaveform(Waveform waveform) {
		wave.setWaveform(waveform);
	}

	/**
	 * @return keys keys
	 */
	public List<Key> getKeys() {
		return keys;
	}

	/**
	 * @return keyboard modulation
	 */
	public Input getModulation() {
		return modulation;
	}

	/**
	 * @return keyboard gate
	 */
	public Output getGate() {
		return gate;
	}
}
package fr.guehenneux.bragi.module.model;

import fr.guehenneux.bragi.Settings;
import fr.guehenneux.bragi.connection.Output;
import fr.guehenneux.bragi.module.view.KeyboardView;
import fr.guehenneux.bragi.wave.SineWave;
import fr.guehenneux.bragi.wave.Wave;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jonathan Guéhenneux
 */
public class Keyboard extends Module {

	private Output output;
	private Output gate;

	private List<Key> keys;
	private Wave wave;

	/**
	 * @param name keyboard name
	 */
	public Keyboard(String name) {

		super(name);

		output = addOutput(name + "_output");
		gate = addOutput(name + "_gate");

		wave = new SineWave(440);

		keys = new ArrayList<>();
		keys.add(new Key(440));
		keys.add(new Key(500));

		new KeyboardView(this);
		start();
	}

	@Override
	public void compute() throws InterruptedException {

		int sampleCount = Settings.INSTANCE.getBufferSizeInFrames();
		double sampleLength = Settings.INSTANCE.getFrameLength();
		float[] samples = wave.getSamples(sampleCount, sampleLength);
		output.write(samples);
	}

	/**
	 * @return keyboard frequency
	 */
	public double getFrequency() {
		return wave.getFrequency();
	}

	/**
	 * @param frequency keyboard frequency
	 */
	public void setFrequency(double frequency) {
		wave.setFrequency(frequency);
	}

	/**
	 * @param wave wave
	 */
	public void setWave(Wave wave) {
		this.wave = wave;
	}

	/**
	 * @return keys keys
	 */
	public List<Key> getKeys() {
		return keys;
	}
}
package fr.guehenneux.bragi.module.model;

import fr.guehenneux.bragi.Settings;
import fr.guehenneux.bragi.connection.Input;
import fr.guehenneux.bragi.connection.Output;
import fr.guehenneux.bragi.module.view.KeyboardView;
import fr.guehenneux.bragi.wave.PulseWave;
import fr.guehenneux.bragi.wave.SawtoothWave;
import fr.guehenneux.bragi.wave.SineWave;
import fr.guehenneux.bragi.wave.SquareWave;
import fr.guehenneux.bragi.wave.Wave;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jonathan Guéhenneux
 */
public class Keyboard extends Module {

  private Input modulation;
	private Output output;
	private Output gate;

	private List<Key> keys;
	private Wave wave;
	private float gateSample;

	/**
	 * @param name keyboard name
	 */
	public Keyboard(String name) {

		super(name);

		modulation = addInput(name + "_modulation");
		output = addOutput(name + "_output");
		gate = addOutput(name + "_gate");

		wave = new PulseWave(0.25f, 440);

		keys = new ArrayList<>();
		keys.add(new Key(440));
		keys.add(new Key(500));

    gateSample = 0.0f;

    new KeyboardView(this);

		start();
	}

	@Override
	public void compute() throws InterruptedException {

		int sampleCount = Settings.INSTANCE.getBufferSizeInFrames();
		double sampleLength = Settings.INSTANCE.getFrameLength();

		if (modulation.isConnected()) {

		  float[] modulationSamples = modulation.read();
		  output.write(wave.getSamples(modulationSamples, sampleCount, sampleLength));

    } else {

      output.write(wave.getSamples(sampleCount, sampleLength));
    }

		if (gate.isConnected()) {
      gate.write(new float[] { gateSample });
    }
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
    gateSample = 1.0f;
	}

  /**
   *
   */
	public void release() {
	  gateSample = -1.0f;
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
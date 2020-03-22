package fr.guehenneux.bragi.module.model;

import java.util.Arrays;

import fr.guehenneux.bragi.Settings;
import fr.guehenneux.bragi.connection.Output;

/**
 * @author Jonathan Guéhenneux
 */
public class WhiteNoiseGenerator extends Module {

  private Output output;

  /**
   * @param name white noise generator name
   */
  public WhiteNoiseGenerator(String name) {

    super(name);

    output = addOutput(name + "_output");
    start();
  }

  @Override
  protected void compute() throws InterruptedException {

    int sampleCount = Settings.INSTANCE.getBufferSizeInFrames();
    float[] outputSamples = new float[sampleCount];

    for (int sampleIndex = 0; sampleIndex < sampleCount; sampleIndex++) {
      outputSamples[sampleIndex] = (float) Math.random();
    }

    output.write(outputSamples);
  }
}
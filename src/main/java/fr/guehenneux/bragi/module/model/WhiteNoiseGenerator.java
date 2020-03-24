package fr.guehenneux.bragi.module.model;

import fr.guehenneux.bragi.Settings;
import fr.guehenneux.bragi.connection.Output;

import static java.lang.Math.random;

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

    output = addPrimaryOutput(name + "_output");
    start();
  }

  @Override
  protected int compute() throws InterruptedException {

    var sampleCount = Settings.INSTANCE.getChunkSize();
    var outputSamples = new float[sampleCount];

    for (var sampleIndex = 0; sampleIndex < sampleCount; sampleIndex++) {
      outputSamples[sampleIndex] = (float) random();
    }

    output.write(outputSamples);

    return sampleCount;
  }
}
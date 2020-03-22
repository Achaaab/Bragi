package fr.guehenneux.bragi.module.model;

import fr.guehenneux.bragi.connection.Input;
import fr.guehenneux.bragi.connection.Output;

/**
 * @author Jonathan Guéhenneux
 */
public class Ditherer extends Module {

  private Input input;
  private Output output;

  /**
   * @param name ditherer name
   */
  public Ditherer(String name) {

    super(name);

    input = addInput(name + "_input");
    output = addOutput(name + "_output");

    start();
  }

  @Override
  protected void compute() throws InterruptedException {

    float[] inputSamples = input.read();
    int sampleCount = inputSamples.length;
    float[] outputSamples = new float[sampleCount];

    for (int sampleIndex = 0; sampleIndex < sampleCount; sampleIndex++) {
      outputSamples[sampleIndex] = inputSamples[sampleIndex] + (float) Math.random() / 100;
    }

    output.write(outputSamples);
  }
}
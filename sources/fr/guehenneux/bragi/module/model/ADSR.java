package fr.guehenneux.bragi.module.model;

import fr.guehenneux.bragi.Settings;
import fr.guehenneux.bragi.connection.Input;
import fr.guehenneux.bragi.connection.Output;
import fr.guehenneux.bragi.module.view.ADSRView;

/**
 * @author Jonathan Guéhenneux
 */
public class ADSR extends Module {

  private Input gate;
  private Output output;

  private double attack;
  private double decay;
  private double sustain;
  private double release;

  private float previousGateSample;
  private double envelopeTime;
  private double releaseTime;

  /**
   * @param name ADSR name
   */
  public ADSR(String name) {

    super(name);

    gate = addInput(name + "_gate");
    output = addOutput(name + "_output");

    attack = 0.50;
    decay = 0.05;
    sustain = 0.5;
    release = 1.0;

    previousGateSample = 0.0f;
    envelopeTime = 0.0;

    new ADSRView(this);
    start();
  }

  @Override
  protected void compute() throws InterruptedException {

    float gateSample = gate.read()[0];

    int sampleCount = Settings.INSTANCE.getBufferSizeInFrames();
    double sampleLength = Settings.INSTANCE.getFrameLength();
    float[] outputSamples = new float[sampleCount];
    double outputSample;

    if (gateSample == 1.0f && previousGateSample != 1.0f) {

      // reset envelope
      envelopeTime = 0.0;

    } else if (gateSample == -1.0f && previousGateSample != -1.0f) {

      // @TODO when releasing before sustain level is reached there is a gap in the sound

      // start release
      releaseTime = envelopeTime;
    }

    previousGateSample = gateSample;

    for (int sampleIndex = 0; sampleIndex < sampleCount; sampleIndex++) {

      if (gateSample == 0.0f) {

        // nothing

        outputSample = 0.0f;

      } else if (gateSample == 1.0f) {

        // attack, decay or sustain

        if (envelopeTime < attack) {
          outputSample = envelopeTime / attack;
        }
        else if (envelopeTime < attack + decay) {
          outputSample = 1.0 + (sustain - 1.0) * (envelopeTime - attack) / decay;
        }
        else {
          outputSample = sustain;
        }

      } else {

        // release

        if (envelopeTime - releaseTime < release) {
          outputSample = sustain * (1 + (releaseTime - envelopeTime) / release);
        } else {
          outputSample = 0.0f;
        }
      }

      outputSamples[sampleIndex] = (float) outputSample;
      envelopeTime += sampleLength;
    }

    output.write(outputSamples);
  }

  /**
   * @return ADSR gate
   */
  public Input getGate() {
    return gate;
  }

  /**
   * @param attack attack time (s)
   */
  public void setAttack(double attack) {
    this.attack = attack;
  }

  /**
   * @param decay decay time (s)
   */
  public void setDecay(double decay) {
    this.decay = decay;
  }

  /**
   * @param sustain sustain level from 0.0 to 1.0
   */
  public void setSustain(double sustain) {
    this.sustain = sustain;
  }

  /**
   * @param release release time (s)
   */
  public void setRelease(double release) {
    this.release = release;
  }
}
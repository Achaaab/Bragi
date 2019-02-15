package fr.guehenneux.bragi.module.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.guehenneux.bragi.Settings;
import fr.guehenneux.bragi.connection.Input;
import fr.guehenneux.bragi.connection.Output;
import fr.guehenneux.bragi.module.view.ADSRView;

/**
 * @author Jonathan Guéhenneux
 */
public class ADSR extends Module {

  public static final float MINIMAL_GAIN = -1.0f;
  public static final float MAXIMAL_GAIN = 1.0f;

  private static final Logger LOGGER = LogManager.getLogger();

  private Input gate;
  private Output output;

  private double attack;
  private double decay;
  private double sustain;
  private double release;

  private double gain;
  private double sampleLength;

  private float previousGateSample;

  private ADSRState state;

  /**
   * @param name ADSR name
   */
  public ADSR(String name) {

    super(name);

    gate = addInput(name + "_gate");
    output = addOutput(name + "_output");

    attack = 40;
    decay = 40;
    sustain = 0.9;
    release = 2;

    gain = MINIMAL_GAIN;
    state = ADSRState.IDLE;
    previousGateSample = 0.0f;

    new ADSRView(this);
    start();
  }

  @Override
  protected void compute() throws InterruptedException {

    float gateSample = gate.read()[0];

    int sampleCount = Settings.INSTANCE.getBufferSizeInFrames();
    sampleLength = Settings.INSTANCE.getFrameLength();
    float[] gains = new float[sampleCount];

    if (gateSample > 0 && previousGateSample <= 0) {
      state = ADSRState.ATTACK;
    } else if (gateSample < 0 && previousGateSample >= 0) {
      state = ADSRState.RELEASE;
    }

    previousGateSample = gateSample;

    for (int sampleIndex = 0; sampleIndex < sampleCount; sampleIndex++) {

      switch (state) {

      case IDLE:
        gain = MINIMAL_GAIN;
        break;

      case SUSTAIN:
        gain = sustain;
        break;

      case ATTACK:
        step(MAXIMAL_GAIN, attack, ADSRState.DECAY);
        break;

      case DECAY:
        step(sustain, decay, ADSRState.SUSTAIN);
        break;

      case RELEASE:
        step(MINIMAL_GAIN, release, ADSRState.IDLE);
        break;
      }

      gains[sampleIndex] = (float) gain;
    }

    output.write(gains);
  }

  /**
   * @param targetGain
   * @param speed
   * @param targetState
   */
  private void step(double targetGain, double speed, ADSRState targetState) {

    if (gain < targetGain) {
      gain = Math.min(gain + speed * sampleLength, targetGain);
    } else {
      gain = Math.max(gain - speed * sampleLength, targetGain);
    }

    if (gain == targetGain) {
      state = targetState;
    }
  }

  /**
   * @return ADSR gate
   */
  public Input getGate() {
    return gate;
  }

  /**
   * Attack speed must be strictly positive.
   *
   * @param attack attack speed (V/s)
   */
  public void setAttack(double attack) {
    this.attack = attack;
  }

  /**
   * Decay speed must be strictly negative.
   *
   * @param decay decay speed (V/s)
   */
  public void setDecay(double decay) {
    this.decay = decay;
  }

  /**
   * Sustain gainSample must be in range [MINIMAL_GAIN, MAXIMAL_GAIN].
   *
   * @param sustain sustain gain
   * @see #MINIMAL_GAIN
   * @see #MAXIMAL_GAIN
   */
  public void setSustain(double sustain) {
    this.sustain = sustain;
  }

  /**
   * Release speed must be strictly negative.
   *
   * @param release release speed (V/s)
   */
  public void setRelease(double release) {
    this.release = release;
  }
}
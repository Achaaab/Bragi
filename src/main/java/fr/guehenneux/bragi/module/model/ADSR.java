package fr.guehenneux.bragi.module.model;

import fr.guehenneux.bragi.Settings;
import fr.guehenneux.bragi.connection.Input;
import fr.guehenneux.bragi.connection.Output;
import fr.guehenneux.bragi.module.view.ADSRView;
import org.slf4j.Logger;

import static fr.guehenneux.bragi.module.model.ADSRState.ATTACK;
import static fr.guehenneux.bragi.module.model.ADSRState.DECAY;
import static fr.guehenneux.bragi.module.model.ADSRState.IDLE;
import static fr.guehenneux.bragi.module.model.ADSRState.RELEASE;
import static fr.guehenneux.bragi.module.model.ADSRState.SUSTAIN;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Jonathan Guéhenneux
 */
public class ADSR extends Module {

	private static final Logger LOGGER = getLogger(ADSR.class);

	public static final float MINIMAL_GAIN = -1.0f;
	public static final float MAXIMAL_GAIN = 1.0f;

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

		gate = addPrimaryInput(name + "_gate");
		output = addOutput(name + "_output");

		attack = 40;
		decay = 40;
		sustain = 0.9;
		release = 2;

		gain = MINIMAL_GAIN;
		state = IDLE;
		previousGateSample = 0.0f;

		new ADSRView(this);
		start();
	}

	@Override
	protected int compute() throws InterruptedException {

		sampleLength = Settings.INSTANCE.getFrameLength();

		var gateSample = gate.read()[0];
		var sampleCount = Settings.INSTANCE.getChunkSize();
		var gains = new float[sampleCount];

		if (gateSample > 0 && previousGateSample <= 0) {
			state = ATTACK;
		} else if (gateSample < 0 && previousGateSample >= 0) {
			state = RELEASE;
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
					step(MAXIMAL_GAIN, attack, DECAY);
					break;

				case DECAY:
					step(sustain, decay, SUSTAIN);
					break;

				case RELEASE:
					step(MINIMAL_GAIN, release, IDLE);
					break;
			}

			gains[sampleIndex] = (float) gain;
		}

		output.write(gains);

		return sampleCount;
	}

	/**
	 * @param targetGain  target gain in volts
	 * @param speed       gain change speed (V/s)
	 * @param targetState new state to set when target gain is reached
	 */
	private void step(double targetGain, double speed, ADSRState targetState) {

		if (gain < targetGain) {
			gain = min(gain + speed * sampleLength, targetGain);
		} else {
			gain = max(gain - speed * sampleLength, targetGain);
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
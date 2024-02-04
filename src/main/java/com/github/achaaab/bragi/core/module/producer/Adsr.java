package com.github.achaaab.bragi.core.module.producer;

import com.github.achaaab.bragi.common.Settings;
import com.github.achaaab.bragi.core.connection.Input;
import com.github.achaaab.bragi.core.connection.Output;
import com.github.achaaab.bragi.core.module.Module;
import com.github.achaaab.bragi.core.module.ModuleCreationException;
import com.github.achaaab.bragi.gui.module.AdsrView;
import org.slf4j.Logger;

import java.lang.reflect.InvocationTargetException;

import static com.github.achaaab.bragi.core.module.producer.AdsrState.DECAY;
import static com.github.achaaab.bragi.core.module.producer.AdsrState.IDLE;
import static com.github.achaaab.bragi.core.module.producer.AdsrState.SUSTAIN;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static javax.swing.SwingUtilities.invokeAndWait;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * ADSR is an envelope generator. It produces a signal that can be sent to the gain input of an VCA or to
 * the modulation input of a VCF.
 * ADSR means Attack, Decay, Sustain and Release.
 * Attack is the time taken for initial run-up of level from nil to peak, beginning when a positive value is read
 * on the gate input.
 * Decay is the time taken for the subsequent run down from the attack level to the designated sustain level.
 * Sustain is the level during the main sequence of the sound's duration, until a negative value is read on the gate
 * port.
 * Release is the time taken for the level to decay from the sustain level to zero after a negative value is read
 * on the gate port.
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.6
 */
public class Adsr extends Module {

	private static final Logger LOGGER = getLogger(Adsr.class);

	public static final String DEFAULT_NAME = "adsr";
	public static final float MINIMAL_GAIN = Settings.INSTANCE.minimalVoltage();
	public static final float MAXIMAL_GAIN = 0;

	private final Input gate;
	private final Output output;

	private double attack;
	private double decay;
	private double sustain;
	private double release;

	private double gain;
	private double sampleLength;

	private float previousGateSample;

	private AdsrState state;

	/**
	 * Creates an ADSR with default name.
	 *
	 * @see #DEFAULT_NAME
	 * @since 0.0.9
	 */
	public Adsr() {
		this(DEFAULT_NAME);
	}

	/**
	 * Creates an ADSR module with specified name.
	 *
	 * @param name ADSR name
	 * @since 0.2.0
	 */
	public Adsr(String name) {

		super(name);

		gate = addPrimaryInput(name + "_gate");
		output = addPrimaryOutput(name + "_output");

		attack = 50;
		decay = 50;
		sustain = -0.5;
		release = 10;

		gain = MINIMAL_GAIN;
		state = IDLE;
		previousGateSample = 0.0f;

		try {
			invokeAndWait(() -> view = new AdsrView(this));
		} catch (InterruptedException | InvocationTargetException cause) {
			throw new ModuleCreationException(cause);
		}
	}

	@Override
	protected int compute() throws InterruptedException {

		sampleLength = Settings.INSTANCE.frameDuration();

		var gateSample = gate.read()[0];
		var sampleCount = Settings.INSTANCE.chunkSize();
		var gains = new float[sampleCount];

		if (gateSample > 0 && previousGateSample <= 0) {
			state = AdsrState.ATTACK;
		} else if (gateSample < 0 && previousGateSample >= 0) {
			state = AdsrState.RELEASE;
		}

		previousGateSample = gateSample;

		for (var sampleIndex = 0; sampleIndex < sampleCount; sampleIndex++) {

			switch (state) {
				case IDLE -> gain = MINIMAL_GAIN;
				case SUSTAIN -> gain = sustain;
				case ATTACK -> step(MAXIMAL_GAIN, attack, DECAY);
				case DECAY -> step(sustain, decay, SUSTAIN);
				case RELEASE -> step(MINIMAL_GAIN, release, IDLE);
			}

			gains[sampleIndex] = (float) gain;
		}

		output.write(gains);

		return sampleCount;
	}

	/**
	 * @param targetGain target gain in Volts
	 * @param speed gain change speed in Volts per second
	 * @param targetState new state to set when target gain is reached
	 * @since 0.2.0
	 */
	private void step(double targetGain, double speed, AdsrState targetState) {

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
	 * @return ADSR gate input
	 * @since 0.2.0
	 */
	public Input gate() {
		return gate;
	}

	/**
	 * @return attack speed in Volts per second
	 * @since 0.2.0
	 */
	public double getAttack() {
		return attack;
	}

	/**
	 * Attack speed must be strictly positive.
	 *
	 * @param attack attack speed in Volts per second
	 * @since 0.2.0
	 */
	public void setAttack(double attack) {
		this.attack = attack;
	}

	/**
	 * @return decay speed in Volts per second
	 * @since 0.2.0
	 */
	public double getDecay() {
		return decay;
	}

	/**
	 * Decay speed must be strictly positive.
	 *
	 * @param decay decay speed in Volts per second
	 * @since 0.2.0
	 */
	public void setDecay(double decay) {
		this.decay = decay;
	}

	/**
	 * @return sustain gain in Volts
	 * @since 0.2.0
	 */
	public double getSustain() {
		return sustain;
	}

	/**
	 * Sustain must be in range [MINIMAL_GAIN, MAXIMAL_GAIN].
	 *
	 * @param sustain sustain gain in Volts
	 * @see #MINIMAL_GAIN
	 * @see #MAXIMAL_GAIN
	 * @since 0.2.0
	 */
	public void setSustain(double sustain) {
		this.sustain = sustain;
	}

	/**
	 * @return release speed in Volts per second
	 * @since 0.2.0
	 */
	public double getRelease() {
		return release;
	}

	/**
	 * Release speed must be strictly positive.
	 *
	 * @param release release speed in Volts per second
	 * @since 0.2.0
	 */
	public void setRelease(double release) {
		this.release = release;
	}
}

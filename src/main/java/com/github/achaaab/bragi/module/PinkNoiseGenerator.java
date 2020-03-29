package com.github.achaaab.bragi.module;

import com.github.achaaab.bragi.common.CircularFloatArray;
import com.github.achaaab.bragi.common.GeometricRandom;
import com.github.achaaab.bragi.common.Normalizer;
import com.github.achaaab.bragi.common.Settings;
import com.github.achaaab.bragi.common.connection.Output;
import org.slf4j.Logger;

import java.util.concurrent.ThreadLocalRandom;

import static com.github.achaaab.bragi.common.ArrayUtils.createFloatArray;
import static com.github.achaaab.bragi.common.ArrayUtils.sum;
import static java.lang.Float.NaN;
import static java.lang.Float.isNaN;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * pink noise generator
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.7
 */
public class PinkNoiseGenerator extends Module {

	private static final Logger LOGGER = getLogger(PinkNoiseGenerator.class);

	public static final String DEFAULT_NAME = "pink_noise_generator";

	private static final int PINK_NOISE_DURATION = 10;
	private static final int PINK_NOISE_SIZE = PINK_NOISE_DURATION * Settings.INSTANCE.frameRate();
	private static final int WHITE_NOISE_COUNT = 16;

	private static final Normalizer NORMALIZER = new Normalizer(
			0.0f, WHITE_NOISE_COUNT,
			Settings.INSTANCE.minimalVoltage(), Settings.INSTANCE.maximalVoltage()
	);

	/**
	 * Creates a pink noise, summing white noises.
	 *
	 * @return created pink noise
	 */
	private static CircularFloatArray createPinkNoise() {

		var uniformRandom = ThreadLocalRandom.current();
		var geometricRandom = new GeometricRandom();

		var pinkNoise = new CircularFloatArray(PINK_NOISE_SIZE);
		var whiteNoises = createFloatArray(PINK_NOISE_SIZE, WHITE_NOISE_COUNT, NaN);

		var randomSampleCount = 2 * PINK_NOISE_SIZE;

		while (randomSampleCount-- > 0) {

			var trials = geometricRandom.searchRandomGeometricCoinFlip();

			var sampleIndex = uniformRandom.nextInt(PINK_NOISE_SIZE);
			var whiteNoiseIndex = trials > WHITE_NOISE_COUNT ? 0 : (int) trials - 1;

			whiteNoises[sampleIndex][whiteNoiseIndex] = uniformRandom.nextFloat();
		}

		for (var whiteNoiseIndex = 0; whiteNoiseIndex < WHITE_NOISE_COUNT; whiteNoiseIndex++) {

			var randomSample = whiteNoises[0][whiteNoiseIndex];

			if (isNaN(randomSample)) {

				randomSample = uniformRandom.nextFloat();
				whiteNoises[0][whiteNoiseIndex] = randomSample;
			}

			for (var sampleIndex = 1; sampleIndex < PINK_NOISE_SIZE; sampleIndex++) {

				if (isNaN(whiteNoises[sampleIndex][whiteNoiseIndex])) {
					whiteNoises[sampleIndex][whiteNoiseIndex] = randomSample;
				} else {
					randomSample = whiteNoises[sampleIndex][whiteNoiseIndex];
				}
			}
		}

		for (var sampleIndex = 0; sampleIndex < PINK_NOISE_SIZE; sampleIndex++) {
			pinkNoise.write(NORMALIZER.normalize(sum(whiteNoises[sampleIndex])));
		}

		return pinkNoise;
	}

	private final Output output;

	private final CircularFloatArray pinkNoise;

	/**
	 * Creates an pink noise generator with default name.
	 *
	 * @see #DEFAULT_NAME
	 * @since 0.0.9
	 */
	public PinkNoiseGenerator() {
		this(DEFAULT_NAME);
	}

	/**
	 * @param name pink noise generator name
	 */
	public PinkNoiseGenerator(String name) {

		super(name);

		output = addPrimaryOutput(name + "_output");

		pinkNoise = createPinkNoise();

		start();
	}

	@Override
	protected int compute() throws InterruptedException {

		var sampleCount = Settings.INSTANCE.chunkSize();
		var samples = new float[sampleCount];
		pinkNoise.read(samples);
		output.write(samples);
		return sampleCount;
	}
}
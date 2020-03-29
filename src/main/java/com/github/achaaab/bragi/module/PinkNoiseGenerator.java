package com.github.achaaab.bragi.module;

import com.github.achaaab.bragi.common.CircularFloatArray;
import com.github.achaaab.bragi.common.Normalizer;
import com.github.achaaab.bragi.common.GeometricRandom;
import com.github.achaaab.bragi.common.Settings;
import com.github.achaaab.bragi.common.connection.Output;
import org.slf4j.Logger;

import java.util.Random;

import static com.github.achaaab.bragi.common.ArrayUtils.createFloatArray;
import static com.github.achaaab.bragi.common.ArrayUtils.sum;
import static java.lang.Float.NaN;
import static java.lang.Float.isNaN;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Pink noise generator.
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.7
 */
public class PinkNoiseGenerator extends Module {

	private static final Logger LOGGER = getLogger(PinkNoiseGenerator.class);

	private static final int PINK_NOISE_LENGTH = Settings.INSTANCE.getFrameRate() * 10;
	private static final int WHITE_NOISE_COUNT = 16;

	private static final Normalizer NORMALIZER = new Normalizer(
			0.0f, WHITE_NOISE_COUNT,
			Settings.INSTANCE.getMinimalVoltage(), Settings.INSTANCE.getMaximalVoltage()
	);

	private static final Random RANDOM = new Random();

	/**
	 * Creates a pink noise, summing white noises.
	 *
	 * @return created pink noise
	 */
	private static CircularFloatArray createPinkNoise() {

		var geometricRandom = new GeometricRandom();

		var pinkNoise = new CircularFloatArray(PINK_NOISE_LENGTH);
		var whiteNoises = createFloatArray(PINK_NOISE_LENGTH, WHITE_NOISE_COUNT, NaN);

		var randomSampleCount = 2 * PINK_NOISE_LENGTH;

		while (randomSampleCount-- > 0) {

			var trials = geometricRandom.searchRandomGeometricCoinFlip();

			var sampleIndex = RANDOM.nextInt(PINK_NOISE_LENGTH);
			var whiteNoiseIndex = trials > WHITE_NOISE_COUNT ? 0 : (int) trials - 1;

			whiteNoises[sampleIndex][whiteNoiseIndex] = RANDOM.nextFloat();
		}

		for (var whiteNoiseIndex = 0; whiteNoiseIndex < WHITE_NOISE_COUNT; whiteNoiseIndex++) {

			var randomSample = whiteNoises[0][whiteNoiseIndex];

			if (isNaN(randomSample)) {

				randomSample = RANDOM.nextFloat();
				whiteNoises[0][whiteNoiseIndex] = randomSample;
			}

			for (var sampleIndex = 1; sampleIndex < PINK_NOISE_LENGTH; sampleIndex++) {

				if (isNaN(whiteNoises[sampleIndex][whiteNoiseIndex])) {
					whiteNoises[sampleIndex][whiteNoiseIndex] = randomSample;
				} else {
					randomSample = whiteNoises[sampleIndex][whiteNoiseIndex];
				}
			}
		}

		for (var sampleIndex = 0; sampleIndex < PINK_NOISE_LENGTH; sampleIndex++) {
			pinkNoise.write(NORMALIZER.normalize(sum(whiteNoises[sampleIndex])));
		}

		return pinkNoise;
	}

	private Output output;

	private CircularFloatArray pinkNoise;

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

		var sampleCount = Settings.INSTANCE.getChunkSize();
		var samples = new float[sampleCount];
		pinkNoise.read(samples);
		output.write(samples);
		return sampleCount;
	}
}
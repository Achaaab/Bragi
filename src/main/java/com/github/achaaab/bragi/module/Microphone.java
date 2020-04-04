package com.github.achaaab.bragi.module;

import com.github.achaaab.bragi.common.ModuleCreationException;
import com.github.achaaab.bragi.common.Normalizer;
import com.github.achaaab.bragi.common.Settings;
import org.slf4j.Logger;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import static javax.sound.sampled.AudioSystem.getTargetDataLine;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * A {@link Microphone} reads samples at {@link AudioFormat} from a {@link TargetDataLine},
 * convert them to {@code float} samples and writes it to its output (1 output per channel).
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.9
 */
public class Microphone extends Module {

	private static final Logger LOGGER = getLogger(Microphone.class);

	public static final String DEFAULT_NAME = "microphone";

	private static final int ONE_BYTE_MIN_VALUE = 0xFF_FF_FF_A0;
	private static final int ONE_BYTE_MAX_VALUE = 0x00_00_00_7F;

	private static final int TWO_BYTES_MIN_VALUE = 0xFF_FF_80_00;
	private static final int TWO_BYTES_MAX_VALUE = 0x00_00_7F_FF;

	private static final Normalizer ONE_BYTE_NORMALIZER = new Normalizer(
			ONE_BYTE_MIN_VALUE, ONE_BYTE_MAX_VALUE,
			Settings.INSTANCE.minimalVoltage(), Settings.INSTANCE.maximalVoltage()
	);

	private static final Normalizer TWO_BYTES_NORMALIZER = new Normalizer(
			TWO_BYTES_MIN_VALUE, TWO_BYTES_MAX_VALUE,
			Settings.INSTANCE.minimalVoltage(), Settings.INSTANCE.maximalVoltage()
	);

	private final TargetDataLine line;

	/**
	 * Creates a microphone with default name.
	 *
	 * @throws ModuleCreationException if no target data line is available
	 * @see #DEFAULT_NAME
	 * @since 0.0.9
	 */
	public Microphone() {
		this(DEFAULT_NAME);
	}

	/**
	 * @param name name of the microphone
	 * @throws ModuleCreationException if no target data line is available
	 */
	public Microphone(String name) {

		super(name);

		addPrimaryOutput(name + "_output_" + outputs.size());

		while (outputs.size() < Settings.INSTANCE.channelCount()) {
			addSecondaryOutput(name + "_output_" + outputs.size());
		}

		var format = new AudioFormat(
				Settings.INSTANCE.frameRate(),
				Settings.INSTANCE.sampleSize() * 8,
				Settings.INSTANCE.channelCount(),
				true, true);

		try {

			line = getTargetDataLine(format);
			line.open(format, Settings.INSTANCE.byteRate() / 100);

		} catch (LineUnavailableException cause) {

			throw new ModuleCreationException(cause);
		}

		line.start();

		start();
	}

	/**
	 * Check if the line buffer is not full.
	 * If it is full, that means it is not read fast enough and data are being lost.
	 */
	private void checkLineBufferHealth() {

		if (line.available() == line.getBufferSize()) {
			LOGGER.warn("microphone line is not read fast enough, thus some data may be lost");
		}
	}

	@Override
	public int compute() throws InterruptedException {

		var frameCount = Settings.INSTANCE.chunkSize();
		var sampleSizeInBytes = Settings.INSTANCE.sampleSize();
		var frameSizeInBytes = Settings.INSTANCE.frameSize();
		var byteCount = frameCount * frameSizeInBytes;
		var channelCount = Settings.INSTANCE.channelCount();

		var input = new byte[byteCount];
		checkLineBufferHealth();
		line.read(input, 0, byteCount);

		var samples = new float[channelCount][frameCount];

		for (var frameIndex = 0; frameIndex < frameCount; frameIndex++) {

			for (var channelIndex = 0; channelIndex < channelCount; channelIndex++) {

				var sampleIndex = frameIndex * frameSizeInBytes + channelIndex * sampleSizeInBytes;
				var sample = 0.0f;

				if (sampleSizeInBytes == 1) {


					var b0 = input[sampleIndex];
					sample = ONE_BYTE_NORMALIZER.normalize(b0);

				} else if (sampleSizeInBytes == 2) {

					var b0 = input[sampleIndex];
					var b1 = input[sampleIndex + 1];
					sample = TWO_BYTES_NORMALIZER.normalize(b1 & 0xFF | b0 << 8);
				}

				samples[channelIndex][frameIndex] = sample;
			}
		}

		for (var channelIndex = 0; channelIndex < channelCount; channelIndex++) {
			outputs.get(channelIndex).write(samples[channelIndex]);
		}

		return frameCount;
	}
}
package com.github.achaaab.bragi.core.module.consumer;

import com.github.achaaab.bragi.core.module.ModuleCreationException;
import com.github.achaaab.bragi.common.Normalizer;
import com.github.achaaab.bragi.common.Settings;
import com.github.achaaab.bragi.core.module.Module;
import org.slf4j.Logger;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.spi.MixerProvider;

import static java.lang.Math.round;
import static javax.sound.sampled.AudioSystem.getLine;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * A {@link Speaker} reads samples from its inputs (1 input per channel), convert them to {@link AudioFormat}
 * and writes them to a {@link SourceDataLine}.
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.9
 */
public class Speaker extends Module {

	private static final Logger LOGGER = getLogger(Speaker.class);

	public static final String DEFAULT_NAME = "speaker";

	private static final int ONE_BYTE_MIN_VALUE = 0xFF_FF_FF_80;
	private static final int ONE_BYTE_MAX_VALUE = 0x00_00_00_7F;

	private static final int TWO_BYTES_MIN_VALUE = 0xFF_FF_80_00;
	private static final int TWO_BYTES_MAX_VALUE = 0x00_00_7F_FF;

	private static final int THREE_BYTES_MIN_VALUE = 0xFF_80_00_00;
	private static final int THREE_BYTES_MAX_VALUE = 0x00_7F_FF_FF;

	private static final int FOUR_BYTES_MIN_VALUE = 0x80_00_00_00;
	private static final int FOUR_BYTES_MAX_VALUE = 0x7F_FF_FF_FF;

	private static final Normalizer ONE_BYTE_NORMALIZER = new Normalizer(
			Settings.INSTANCE.minimalVoltage(), Settings.INSTANCE.maximalVoltage(),
			ONE_BYTE_MIN_VALUE, ONE_BYTE_MAX_VALUE
	);

	private static final Normalizer TWO_BYTES_NORMALIZER = new Normalizer(
			Settings.INSTANCE.minimalVoltage(), Settings.INSTANCE.maximalVoltage(),
			TWO_BYTES_MIN_VALUE, TWO_BYTES_MAX_VALUE
	);

	private static final Normalizer THREE_BYTES_NORMALIZER = new Normalizer(
			Settings.INSTANCE.minimalVoltage(), Settings.INSTANCE.maximalVoltage(),
			THREE_BYTES_MIN_VALUE, THREE_BYTES_MAX_VALUE
	);

	private static final Normalizer FOUR_BYTES_NORMALIZER = new Normalizer(
			Settings.INSTANCE.minimalVoltage(), Settings.INSTANCE.maximalVoltage(),
			FOUR_BYTES_MIN_VALUE, FOUR_BYTES_MAX_VALUE
	);

	private final SourceDataLine line;

	/**
	 * Creates a speaker with default name.
	 *
	 * @throws ModuleCreationException if no source data line is available
	 * @see #DEFAULT_NAME
	 * @since 0.0.9
	 */
	public Speaker() {
		this(DEFAULT_NAME);
	}

	/**
	 * @param name name of the speaker to create
	 * @throws ModuleCreationException if no source data line is available
	 */
	public Speaker(String name) {

		super(name);

		addPrimaryInput(name + "_input_" + inputs.size());

		while (inputs.size() < Settings.INSTANCE.channelCount()) {
			addSecondaryInput(name + "_input_" + inputs.size());
		}

		var audioFormat = new AudioFormat(
				Settings.INSTANCE.frameRate(),
				Settings.INSTANCE.sampleSize() * 8,
				Settings.INSTANCE.channelCount(),
				true, true);

		var lineInformation = new Info(SourceDataLine.class, audioFormat);

		try {

			line = (SourceDataLine) getLine(lineInformation);
			line.open(audioFormat, Settings.INSTANCE.byteRate() / 50);

		} catch (LineUnavailableException cause) {

			throw new ModuleCreationException(cause);
		}

		line.start();

		start();
	}

	/**
	 * Check if the line buffer is not empty.
	 * If it is empty, that means it is not written fast enough and sound will not be continuous.
	 */
	private void checkLineBufferHealth() {

		if (line.available() == line.getBufferSize()) {
			LOGGER.warn("speaker line is not written fast enough, thus some discontinuities in the audio may be heard");
		}
	}

	@Override
	public int compute() throws InterruptedException {

		var channelCount = Settings.INSTANCE.channelCount();

		var samples = new float[channelCount][];

		for (var channelIndex = 0; channelIndex < channelCount; channelIndex++) {
			samples[channelIndex] = inputs.get(channelIndex).read();
		}

		var data = mix(samples);

		checkLineBufferHealth();

		line.write(data, 0, data.length);

		return samples[0].length;
	}

	/**
	 * @param samples samples to mix
	 * @return mixed samples
	 */
	private static byte[] mix(float[][] samples) {

		var sampleSize = Settings.INSTANCE.sampleSize();
		var channelCount = samples.length;
		var frameCount = samples[0].length;

		var mix = new byte[frameCount * channelCount * sampleSize];

		var byteIndex = 0;

		byte b0;
		byte b1;
		byte b2;
		byte b3;

		for (var frameIndex = 0; frameIndex < frameCount; frameIndex++) {

			for (var channelSamples : samples) {

				var sample = channelSamples == null ? 0.0f : channelSamples[frameIndex];

				int normalizedSample;

				switch (sampleSize) {

					case 1:
						normalizedSample = round(ONE_BYTE_NORMALIZER.normalize(sample));
						b0 = (byte) normalizedSample;
						mix[byteIndex++] = b0;
						break;

					case 2:
						normalizedSample = round(TWO_BYTES_NORMALIZER.normalize(sample));
						b0 = (byte) (normalizedSample >> 8);
						b1 = (byte) normalizedSample;
						mix[byteIndex++] = b0;
						mix[byteIndex++] = b1;
						break;

					case 3:
						normalizedSample = round(THREE_BYTES_NORMALIZER.normalize(sample));
						b0 = (byte) (normalizedSample >> 16);
						b1 = (byte) ((normalizedSample >> 8) & 0xFF);
						b2 = (byte) (normalizedSample & 0xFF);
						mix[byteIndex++] = b0;
						mix[byteIndex++] = b1;
						mix[byteIndex++] = b2;
						break;

					case 4:
						normalizedSample = round(FOUR_BYTES_NORMALIZER.normalize(sample));
						b0 = (byte) (normalizedSample >> 24);
						b1 = (byte) ((normalizedSample >> 16) & 0xFF);
						b2 = (byte) ((normalizedSample >> 8) & 0xFF);
						b3 = (byte) (normalizedSample & 0xFF);
						mix[byteIndex++] = b0;
						mix[byteIndex++] = b1;
						mix[byteIndex++] = b2;
						mix[byteIndex++] = b3;
						break;

					default:
						break;
				}
			}
		}

		return mix;
	}
}
package com.github.achaaab.bragi.core.module.consumer;

import com.github.achaaab.bragi.common.Interpolator;
import com.github.achaaab.bragi.common.Normalizer;
import com.github.achaaab.bragi.common.Settings;
import com.github.achaaab.bragi.core.module.Module;
import com.github.achaaab.bragi.core.module.ModuleCreationException;
import org.slf4j.Logger;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.SourceDataLine;

import static com.github.achaaab.bragi.common.Interpolator.CUBIC_HERMITE_SPLINE;
import static java.lang.Math.round;
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

	private static final Interpolator INTERPOLATOR = CUBIC_HERMITE_SPLINE;

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
			ONE_BYTE_MIN_VALUE, ONE_BYTE_MAX_VALUE);

	private static final Normalizer TWO_BYTES_NORMALIZER = new Normalizer(
			Settings.INSTANCE.minimalVoltage(), Settings.INSTANCE.maximalVoltage(),
			TWO_BYTES_MIN_VALUE, TWO_BYTES_MAX_VALUE);

	private static final Normalizer THREE_BYTES_NORMALIZER = new Normalizer(
			Settings.INSTANCE.minimalVoltage(), Settings.INSTANCE.maximalVoltage(),
			THREE_BYTES_MIN_VALUE, THREE_BYTES_MAX_VALUE);

	private static final Normalizer FOUR_BYTES_NORMALIZER = new Normalizer(
			Settings.INSTANCE.minimalVoltage(), Settings.INSTANCE.maximalVoltage(),
			FOUR_BYTES_MIN_VALUE, FOUR_BYTES_MAX_VALUE);

	private final int sourceSampleRate;

	private SourceDataLine line;
	private SourceDataLine newLine;

	private byte[] data;

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
	 * @since 0.2.0
	 */
	public Speaker(String name) {

		super(name);

		addPrimaryInput(name + "_input_" + inputs.size());

		while (inputs.size() < 8) {
			addSecondaryInput(name + "_input_" + inputs.size());
		}

		sourceSampleRate = Settings.INSTANCE.frameRate();

		line = null;
		newLine = null;
		data = null;
	}

	@Override
	public void configure() {

		var outputLine = synthesizer.configuration().outputLine();

		if (outputLine != line) {
			newLine = outputLine;
		}
	}

	/**
	 * Called when a new line is configured. Drains and stops the current line, then switch to the new line and starts
	 * it.
	 *
	 * @since 0.2.0
	 */
	private void switchLine() {

		LOGGER.info("switch line");

		if (line != null) {

			line.drain();
			line.stop();
		}

		line = newLine;
		newLine = null;

		line.start();

		var format = line.getFormat();

		var frameRate = format.getSampleRate();
		var chunkDuration = Settings.INSTANCE.chunkDuration();
		var frameCount = round(frameRate * chunkDuration);
		var frameSize = format.getFrameSize();
		var dataLength = frameCount * frameSize;

		data = new byte[dataLength];
	}

	/**
	 * Check if the line buffer is not empty.
	 * If it is empty, that means it is not written fast enough and sound will not be continuous.
	 *
	 * @since 0.2.0
	 */
	private void checkLineBufferHealth() {

		if (line.available() == line.getBufferSize()) {
			LOGGER.warn("Speaker line is not written fast enough: some discontinuities in the audio may be heard.");
		}
	}

	@Override
	public int compute() throws InterruptedException {

		if (newLine != null) {
			switchLine();
		}

		var format = line.getFormat();
		var channelCount = format.getChannels();
		var sampleRate = format.getSampleRate();

		checkLineBufferHealth();

		var samples = new float[channelCount][];

		for (var channelIndex = 0; channelIndex < inputs.size(); channelIndex++) {

			var channelSamples = inputs.get(channelIndex).read();

			if (channelIndex < channelCount) {

				samples[channelIndex] = sampleRate == sourceSampleRate ?
						channelSamples :
						INTERPOLATOR.interpolate(channelSamples, sourceSampleRate, sampleRate);
			}
		}

		var byteCount = mix(samples);

		line.write(data, 0, byteCount);

		return samples[0].length;
	}

	/**
	 * @param samples samples to mix
	 * @return number of data bytes
	 * @since 0.2.0
	 */
	private int mix(float[][] samples) {

		var format = line.getFormat();
		var sampleSize = format.getSampleSizeInBits() / 8;
		var channelCount = format.getChannels();
		var frameCount = samples[0].length;
		var byteCount = frameCount * channelCount * sampleSize;

		if (data == null || data.length < byteCount) {
			data = new byte[byteCount];
		}

		var dataIndex = 0;

		byte b0;
		byte b1;
		byte b2;
		byte b3;

		for (var frameIndex = 0; frameIndex < frameCount; frameIndex++) {

			for (var channelSamples : samples) {

				var sample = channelSamples == null ? 0.0f : channelSamples[frameIndex];

				int normalizedSample;

				switch (sampleSize) {

					case 1 -> {
						normalizedSample = round(ONE_BYTE_NORMALIZER.normalize(sample));
						b0 = (byte) normalizedSample;
						data[dataIndex++] = b0;
					}

					case 2 -> {
						normalizedSample = round(TWO_BYTES_NORMALIZER.normalize(sample));
						b0 = (byte) (normalizedSample >> 8);
						b1 = (byte) normalizedSample;
						data[dataIndex++] = b0;
						data[dataIndex++] = b1;
					}

					case 3 -> {
						normalizedSample = round(THREE_BYTES_NORMALIZER.normalize(sample));
						b0 = (byte) (normalizedSample >> 16);
						b1 = (byte) ((normalizedSample >> 8) & 0xFF);
						b2 = (byte) (normalizedSample & 0xFF);
						data[dataIndex++] = b0;
						data[dataIndex++] = b1;
						data[dataIndex++] = b2;
					}

					case 4 -> {
						normalizedSample = round(FOUR_BYTES_NORMALIZER.normalize(sample));
						b0 = (byte) (normalizedSample >> 24);
						b1 = (byte) ((normalizedSample >> 16) & 0xFF);
						b2 = (byte) ((normalizedSample >> 8) & 0xFF);
						b3 = (byte) (normalizedSample & 0xFF);
						data[dataIndex++] = b0;
						data[dataIndex++] = b1;
						data[dataIndex++] = b2;
						data[dataIndex++] = b3;
					}

					default -> {
					}
				}
			}
		}

		return byteCount;
	}
}

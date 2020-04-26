package com.github.achaaab.bragi.core.module.producer;

import com.github.achaaab.bragi.common.Interpolator;
import com.github.achaaab.bragi.common.Normalizer;
import com.github.achaaab.bragi.common.Settings;
import com.github.achaaab.bragi.core.module.Module;
import com.github.achaaab.bragi.core.module.ModuleCreationException;
import org.slf4j.Logger;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.TargetDataLine;

import static com.github.achaaab.bragi.common.Interpolator.CUBIC_HERMITE_SPLINE;
import static java.lang.Math.round;
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

	private static final Interpolator INTERPOLATOR = CUBIC_HERMITE_SPLINE;

	private static final int ONE_BYTE_MIN_VALUE = 0xFF_FF_FF_80;
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

	private final int targetSampleRate;

	private TargetDataLine line;
	private TargetDataLine newLine;
	private byte[] data;

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

		while (outputs.size() < 8) {
			addSecondaryOutput(name + "_output_" + outputs.size());
		}

		targetSampleRate = Settings.INSTANCE.frameRate();

		line = null;
		newLine = null;
		data = null;
	}

	@Override
	public void configure() {

		var inputLine = synthesizer.configuration().inputLine();

		if (inputLine != line) {
			newLine = inputLine;
		}
	}

	/**
	 * Called when a new line is configured. Drains and stops the current line,
	 * then switches to the new line and starts it.
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

		if (newLine != null) {
			switchLine();
		}

		var format = line.getFormat();
		var sampleSize = format.getSampleSizeInBits();
		var frameSize = format.getFrameSize();
		var channelCount = format.getChannels();
		var sampleRate = round(format.getSampleRate());

		checkLineBufferHealth();

		var byteCount = line.read(data, 0, data.length);
		var frameCount = byteCount / frameSize;

		var chunk = new float[channelCount][frameCount];

		var dataIndex = 0;

		for (var frameIndex = 0; frameIndex < frameCount; frameIndex++) {

			for (var channelIndex = 0; channelIndex < channelCount; channelIndex++) {

				var sample = 0.0f;

				if (sampleSize == 8) {

					var b0 = data[dataIndex++];
					sample = ONE_BYTE_NORMALIZER.normalize(b0);

				} else if (sampleSize == 16) {

					var b0 = data[dataIndex++];
					var b1 = data[dataIndex++];
					sample = TWO_BYTES_NORMALIZER.normalize(b1 & 0xFF | b0 << 8);
				}

				chunk[channelIndex][frameIndex] = sample;
			}
		}

		for (var channelIndex = 0; channelIndex < channelCount; channelIndex++) {

			outputs.get(channelIndex).write(sampleRate == targetSampleRate ?
					chunk[channelIndex] :
					INTERPOLATOR.interpolate(chunk[channelIndex], sampleRate, targetSampleRate));
		}

		return frameCount;
	}
}
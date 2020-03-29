package fr.guehenneux.bragi.module;

import fr.guehenneux.bragi.common.Normalizer;
import fr.guehenneux.bragi.common.Settings;
import org.slf4j.Logger;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import static javax.sound.sampled.AudioSystem.getTargetDataLine;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Jonathan Guéhenneux
 */
public class Microphone extends Module {

	private static final Logger LOGGER = getLogger(Microphone.class);

	private static final int ONE_BYTE_MIN_VALUE = 0xFF_FF_FF_A0;
	private static final int ONE_BYTE_MAX_VALUE = 0x00_00_00_7F;

	private static final int TWO_BYTES_MIN_VALUE = 0xFF_FF_80_00;
	private static final int TWO_BYTES_MAX_VALUE = 0x00_00_7F_FF;

	private static final Normalizer ONE_BYTE_NORMALIZER = new Normalizer(
			ONE_BYTE_MIN_VALUE, ONE_BYTE_MAX_VALUE,
			Settings.INSTANCE.getMinimalVoltage(), Settings.INSTANCE.getMaximalVoltage()
	);

	private static final Normalizer TWO_BYTES_NORMALIZER = new Normalizer(
			TWO_BYTES_MIN_VALUE, TWO_BYTES_MAX_VALUE,
			Settings.INSTANCE.getMinimalVoltage(), Settings.INSTANCE.getMaximalVoltage()
	);

	private TargetDataLine targetDataLine;

	/**
	 * @param name name of the microphone
	 * @throws LineUnavailableException if no target data line is available
	 */
	public Microphone(String name) throws LineUnavailableException {

		super(name);

		addPrimaryOutput(name + "_output_" + outputs.size());

		while (outputs.size() < Settings.INSTANCE.getChannelCount()) {
			addSecondaryOutput(name + "_output_" + outputs.size());
		}

		var format = new AudioFormat(
				Settings.INSTANCE.getFrameRate(),
				Settings.INSTANCE.getSampleSize() * 8,
				Settings.INSTANCE.getChannelCount(),
				true, true);

		targetDataLine = getTargetDataLine(format);
		targetDataLine.open(format, Settings.INSTANCE.getByteRate() / 100);
		targetDataLine.start();

		start();
	}

	/**
	 * Check if the line buffer is not full.
	 * If it is full, that means it is not read fast enough and data are being lost.
	 */
	private void checkLineBufferHealth() {

		if (targetDataLine.available() == targetDataLine.getBufferSize()) {
			LOGGER.warn("microphone line is not read fast enough, some data may be lost");
		}
	}

	@Override
	public int compute() throws InterruptedException {

		var frameCount = Settings.INSTANCE.getChunkSize();
		var sampleSizeInBytes = Settings.INSTANCE.getSampleSize();
		var frameSizeInBytes = Settings.INSTANCE.getFrameSize();
		var byteCount = frameCount * frameSizeInBytes;
		var channelCount = Settings.INSTANCE.getChannelCount();

		var input = new byte[byteCount];
		checkLineBufferHealth();
		targetDataLine.read(input, 0, byteCount);

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
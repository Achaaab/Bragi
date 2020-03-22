package fr.guehenneux.bragi.module.model;

import fr.guehenneux.bragi.Settings;
import org.slf4j.Logger;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Jonathan Guéhenneux
 */
public class Microphone extends Module {

	private static final Logger LOGGER = getLogger(Microphone.class);

	private TargetDataLine targetDataLine;

	/**
	 * @param name name of the microphone
	 * @throws LineUnavailableException if no target data line is available
	 */
	public Microphone(String name) throws LineUnavailableException {

		super(name);

		while (outputs.size() < Settings.INSTANCE.getChannels()) {
			addOutput(name + "_output_" + outputs.size());
		}

		AudioFormat format = new AudioFormat(Settings.INSTANCE.getFrameRate(), Settings.INSTANCE.getSampleSize() * 8,
				Settings.INSTANCE.getChannels(), true, true);

		targetDataLine = AudioSystem.getTargetDataLine(format);
		targetDataLine.open(format, Settings.INSTANCE.getFrameRate() * Settings.INSTANCE.getFrameSizeInBytes() / 10);

		targetDataLine.start();
		start();
	}

	/**
	 *
	 */
	private void checkLineBufferHealth() {

		int available = targetDataLine.available();
		int bufferSize = targetDataLine.getBufferSize();

		if (available == 0) {
			LOGGER.warn("buffer underrun");
		} else if (available == bufferSize) {
			LOGGER.warn("buffer overrun");
		}
	}

	@Override
	public int compute() throws InterruptedException {

		var frameCount = Settings.INSTANCE.getBufferSizeInFrames();
		var sampleSizeInBytes = Settings.INSTANCE.getSampleSize();
		var frameSizeInBytes = Settings.INSTANCE.getFrameSizeInBytes();
		var byteCount = frameCount * frameSizeInBytes;
		var channelCount = Settings.INSTANCE.getChannels();

		var input = new byte[byteCount];
		checkLineBufferHealth();
		targetDataLine.read(input, 0, byteCount);

		var samples = new float[channelCount][frameCount];

		for (var frameIndex = 0; frameIndex < frameCount; frameIndex++) {

			for (var channelIndex = 0; channelIndex < channelCount; channelIndex++) {

				var sampleIndex = frameIndex * frameSizeInBytes + channelIndex * sampleSizeInBytes;
				var sample = 0.0f;

				if (sampleSizeInBytes == 1) {

					var b0 = input[sampleIndex + 0];
					sample = (float) b0 / Byte.MAX_VALUE;

				} else if (sampleSizeInBytes == 2) {

					var b0 = input[sampleIndex + 1];
					var b1 = input[sampleIndex + 0];
					sample = (float) (b0 & 0xFF | b1 << 8) / Short.MAX_VALUE;
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
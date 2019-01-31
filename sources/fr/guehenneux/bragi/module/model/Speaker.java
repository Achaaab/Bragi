package fr.guehenneux.bragi.module.model;

import fr.guehenneux.bragi.Settings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 * @author Jonathan Guéhenneux
 */
public class Speaker extends Module {

	private static final Logger LOGGER = LogManager.getLogger();

	private static final int ONE_BYTE_MAX_VALUE = 1 << 7 - 1;
	private static final int TWO_BYTES_MAX_VALUE = 1 << 15 - 1;
	private static final int THREE_BYTES_MAX_VALUE = 1 << 23 - 1;

	private SourceDataLine sourceDataLine;

	/**
	 * @param name
	 * @throws LineUnavailableException
	 */
	public Speaker(String name) throws LineUnavailableException {

		super(name);

		while (inputs.size() < Settings.INSTANCE.getChannels()) {
			addInput(name + "_input_" + inputs.size());
		}

		AudioFormat format = new AudioFormat(Settings.INSTANCE.getFrameRate(), Settings.INSTANCE.getSampleSize() * 8,
				Settings.INSTANCE.getChannels(), true, true);

		Info info = new Info(SourceDataLine.class, format);

		sourceDataLine = (SourceDataLine) AudioSystem.getLine(info);
		sourceDataLine.open(format, Settings.INSTANCE.getFrameRate() * Settings.INSTANCE.getFrameSizeInBytes() / 20);
		sourceDataLine.start();

		start();
	}

	/**
	 *
	 */
	private void checkLineBufferHealth() {

		int available = sourceDataLine.available();
		int bufferSize = sourceDataLine.getBufferSize();

		if (available == bufferSize) {
			LOGGER.warn("buffer underrun");
		}
	}

	@Override
	public void compute() throws InterruptedException {

		int channelCount = Settings.INSTANCE.getChannels();
		float[][] samples = new float[channelCount][];

		for (int channelIndex = 0; channelIndex < channelCount; channelIndex++) {
			samples[channelIndex] = inputs.get(channelIndex).read();
		}

		byte[] data = mix(samples);
		checkLineBufferHealth();
		sourceDataLine.write(data, 0, data.length);
	}

	/**
	 * @param samples
	 * @return
	 */
	private static byte[] mix(float[][] samples) {

		int channelCount = samples.length;
		int frameCount = samples[0].length;

		int frameIndex;
		int channelIndex;

		float sample;
		int normalizedSample;

		int sampleSizeInBytes = Settings.INSTANCE.getSampleSize();

		byte b0, b1, b2;
		byte[] mix = new byte[frameCount * channelCount * sampleSizeInBytes];
		int byteIndex = 0;

		for (frameIndex = 0; frameIndex < frameCount; frameIndex++) {

			for (channelIndex = 0; channelIndex < channelCount; channelIndex++) {

				sample = samples[channelIndex][frameIndex];
				sample = Math.max(-1.0f, Math.min(1.0f, sample));

				switch (sampleSizeInBytes) {

					case 1:
						normalizedSample = (int) (ONE_BYTE_MAX_VALUE * sample);
						b0 = (byte) normalizedSample;
						mix[byteIndex++] = b0;
						break;

					case 2:
						normalizedSample = (int) (TWO_BYTES_MAX_VALUE * sample);
						b0 = (byte) (normalizedSample >> 8);
						b1 = (byte) (normalizedSample & 0xFF);
						mix[byteIndex++] = b0;
						mix[byteIndex++] = b1;
						break;

					case 3:
						normalizedSample = (int) (THREE_BYTES_MAX_VALUE * (double) sample);
						b0 = (byte) (normalizedSample >> 16);
						b1 = (byte) ((normalizedSample >> 8) & 0xFF);
						b2 = (byte) (normalizedSample & 0xFF);
						mix[byteIndex++] = b0;
						mix[byteIndex++] = b1;
						mix[byteIndex++] = b2;
						break;

					default:
						break;
				}
			}
		}

		return mix;
	}
}
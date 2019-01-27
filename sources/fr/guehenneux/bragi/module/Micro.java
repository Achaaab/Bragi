package fr.guehenneux.bragi.module;

import fr.guehenneux.bragi.Settings;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

/**
 * @author Jonathan Guéhenneux
 */
public class Micro extends Module {

	private TargetDataLine targetDataLine;

	/**
	 * @param name
	 * @throws LineUnavailableException
	 */
	public Micro(String name) throws LineUnavailableException {

		super(name);

		while (outputs.size() < Settings.INSTANCE.getChannels()) {
			addOutut();
		}

		AudioFormat format = new AudioFormat(Settings.INSTANCE.getFrameRate(), Settings.INSTANCE.getSampleSize() * 8,
				Settings.INSTANCE.getChannels(), true, true);

		targetDataLine = AudioSystem.getTargetDataLine(format);
		targetDataLine.open(format, Settings.INSTANCE.getFrameRate() * Settings.INSTANCE.getFrameSizeInBytes() / 10);

		targetDataLine.start();
	}

	@Override
	public void compute() throws InterruptedException {

		int frameCount = Settings.INSTANCE.getBufferSizeInFrames();
		int sampleSizeInBytes = Settings.INSTANCE.getSampleSize();
		int frameSizeInBytes = Settings.INSTANCE.getFrameSizeInBytes();
		int byteCount = frameCount * frameSizeInBytes;
		int channelCount = Settings.INSTANCE.getChannels();

		byte[] input = new byte[byteCount];
		targetDataLine.read(input, 0, byteCount);

		float[][] samples = new float[channelCount][frameCount];

		int sampleIndex;

		byte b0, b1;
		float sample;

		for (int frameIndex = 0; frameIndex < frameCount; frameIndex++) {

			for (int channelIndex = 0; channelIndex < channelCount; channelIndex++) {

				sampleIndex = frameIndex * frameSizeInBytes + channelIndex * sampleSizeInBytes;

				if (sampleSizeInBytes == 1) {

					b0 = input[sampleIndex + 0];
					sample = (float) b0 / Byte.MAX_VALUE;

				} else if (sampleSizeInBytes == 2) {

					b0 = input[sampleIndex + 1];
					b1 = input[sampleIndex + 0];

					sample = (float) (b0 & 0xFF | b1 << 8) / Short.MAX_VALUE;

				} else {

					sample = 0.0f;
				}

				samples[channelIndex][frameIndex] = sample;
			}
		}

		for (int channelIndex = 0; channelIndex < channelCount; channelIndex++) {
			outputs.get(channelIndex).write(samples[channelIndex]);
		}
	}
}
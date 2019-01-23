package fr.guehenneux.audio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

/**
 * @author Jonathan Guéhenneux
 */
public class Micro extends Module {

	private TargetDataLine targetDataLine;

	private static final int MAX_CHANNEL_COUNT = 2;

	private OutputPort[] outputPorts;

	public Micro(String name) throws LineUnavailableException {

		super(name);

		outputPorts = new OutputPort[MAX_CHANNEL_COUNT];

		for (int channelIndex = 0; channelIndex < MAX_CHANNEL_COUNT; channelIndex++) {
			outputPorts[channelIndex] = new OutputPort();
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
			outputPorts[channelIndex].write(samples[channelIndex]);
		}
	}

	/**
	 * @return outputPorts
	 */
	public OutputPort[] getOutputPorts() {
		return outputPorts;
	}
}
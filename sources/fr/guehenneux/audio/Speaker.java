package fr.guehenneux.audio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 * 
 * @author GUEHENNEUX
 * 
 */
public class Speaker extends Module {

	public static final int MAX_CHANNEL_COUNT = 2;

	private InputPort[] inputPorts;

	private SourceDataLine ligne;

	private static final int ONE_BYTE_MAX_VALUE = 1 << 7 - 1;

	private static final int TWO_BYTES_MAX_VALUE = 1 << 15 - 1;

	private static final int THREE_BYTES_MAX_VALUE = 1 << 23 - 1;

	/**
	 * 
	 * @param name
	 * @throws LineUnavailableException
	 */
	public Speaker(String name) throws LineUnavailableException {

		super(name);

		inputPorts = new InputPort[MAX_CHANNEL_COUNT];

		for (int channelIndex = 0; channelIndex < MAX_CHANNEL_COUNT; channelIndex++) {
			inputPorts[channelIndex] = new InputPort();
		}

		FormatAudio informationsFormat = FormatAudio.getInstance();
		AudioFormat format = new AudioFormat(informationsFormat.getFrameRate(), informationsFormat.getSampleSize() * 8,
				informationsFormat.getChannels(), true, true);

		Info info = new Info(SourceDataLine.class, format);

		ligne = (SourceDataLine) AudioSystem.getLine(info);
		ligne.open(format, informationsFormat.getFrameRate() * informationsFormat.getFrameSizeInBytes() / 10);
		ligne.start();
	}

	public void compute() {

		int channelCount = FormatAudio.getInstance().getChannels();
		float[][] samples = new float[channelCount][];

		for (int channelIndex = 0; channelIndex < channelCount; channelIndex++) {
			samples[channelIndex] = inputPorts[channelIndex].read();
		}

		byte[] donneesConverties = mix(samples);
		ligne.write(donneesConverties, 0, donneesConverties.length);

	}

	private static byte[] mix(float[][] samples) {

		int channelCount = samples.length;
		int frameCount = samples[0].length;

		int frameIndex;
		int channelIndex;

		float sample;
		int sampleNormalise;

		int sampleSizeInBytes = FormatAudio.getInstance().getSampleSize();

		byte b0, b1, b2;
		byte[] mix = new byte[frameCount * channelCount * sampleSizeInBytes];
		int byteIndex = 0;

		for (frameIndex = 0; frameIndex < frameCount; frameIndex++) {

			for (channelIndex = 0; channelIndex < channelCount; channelIndex++) {

				sample = samples[channelIndex][frameIndex];

				switch (sampleSizeInBytes) {

				case 1:

					sampleNormalise = (int) (ONE_BYTE_MAX_VALUE * sample);

					b0 = (byte) sampleNormalise;

					mix[byteIndex++] = b0;

					break;

				case 2:

					sampleNormalise = (int) (TWO_BYTES_MAX_VALUE * sample);

					b0 = (byte) (sampleNormalise >> 8);
					b1 = (byte) (sampleNormalise & 0xFF);

					mix[byteIndex++] = b0;
					mix[byteIndex++] = b1;

					break;

				case 3:

					sampleNormalise = (int) (THREE_BYTES_MAX_VALUE * (double) sample);

					b0 = (byte) (sampleNormalise >> 16);
					b1 = (byte) ((sampleNormalise >> 8) & 0xFF);
					b2 = (byte) (sampleNormalise & 0xFF);

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

	/**
	 * @return the input ports
	 */
	public InputPort[] getInputPorts() {
		return inputPorts;
	}

}

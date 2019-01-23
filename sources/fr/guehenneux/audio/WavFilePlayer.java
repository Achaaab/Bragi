package fr.guehenneux.audio;

import java.io.File;
import java.io.IOException;

/**
 * @author Jonathan Guéhenneux
 */
public class WavFilePlayer extends Module implements Player {

	private static final int MAX_CHANNEL_COUNT = 2;

	private OutputPort[] outputPorts;

	private WavFile wavFile;
	private int offset;
	private int channelCount;

	/**
	 *
	 * @param name
	 * @param file
	 * @throws IOException
	 * @throws CorruptWavFileException
	 */
	public WavFilePlayer(String name, File file) throws IOException,
			CorruptWavFileException {

		super(name);

		outputPorts = new OutputPort[MAX_CHANNEL_COUNT];

		for (int channelIndex = 0; channelIndex < MAX_CHANNEL_COUNT; channelIndex++) {
			outputPorts[channelIndex] = new OutputPort();
		}

		wavFile = new WavFile(file);
		channelCount = wavFile.getChannelCount();
		offset = 0;
	}

	@Override
	public void compute() throws InterruptedException {

		int bufferSizeInFrames = Settings.INSTANCE.getBufferSizeInFrames();
		float[][] samples = new float[channelCount][bufferSizeInFrames];

		try {

			int count = wavFile.read(samples, offset);

			if (count <= 0) {
				offset = 0;
			} else {
				offset += count;
			}

		} catch (IOException ioException) {

			ioException.printStackTrace();
		}

		for (int channelIndex = 0; channelIndex < channelCount; channelIndex++) {
			outputPorts[channelIndex].write(samples[channelIndex]);
		}
	}

	/**
	 * @return
	 */
	public OutputPort[] getOutputPorts() {
		return outputPorts;
	}

	@Override
	public void pause() {

	}

	@Override
	public void play() {

	}

	@Override
	public void setTime(double time) {

	}

	@Override
	public void stop() {

	}
}
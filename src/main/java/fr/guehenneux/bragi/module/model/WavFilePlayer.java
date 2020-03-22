package fr.guehenneux.bragi.module.model;

import fr.guehenneux.bragi.CorruptWavFileException;
import fr.guehenneux.bragi.Settings;
import fr.guehenneux.bragi.WavFile;

import java.io.File;
import java.io.IOException;

/**
 * @author Jonathan Guéhenneux
 */
public class WavFilePlayer extends Module implements Player {

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
	public WavFilePlayer(String name, File file) throws IOException, CorruptWavFileException {

		super(name);

		while (outputs.size() < Settings.INSTANCE.getChannels()) {
			addOutput(name + "_output_" + outputs.size());
		}

		wavFile = new WavFile(file);
		channelCount = wavFile.getChannelCount();
		offset = 0;

		start();
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

		} catch (IOException cause) {

			throw new RuntimeException(cause);
		}

		for (int channelIndex = 0; channelIndex < channelCount; channelIndex++) {
			outputs.get(channelIndex).write(samples[channelIndex]);
		}
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
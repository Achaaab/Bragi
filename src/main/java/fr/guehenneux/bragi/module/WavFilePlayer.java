package fr.guehenneux.bragi.module;

import fr.guehenneux.bragi.common.MalformedWavFileException;
import fr.guehenneux.bragi.common.Settings;
import fr.guehenneux.bragi.common.WavFile;

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
	 * @param name name of the wav file player
	 * @param file wav file to read
	 * @throws IOException IO exception while reading the wav file
	 * @throws MalformedWavFileException if the wav file is malformed
	 */
	public WavFilePlayer(String name, File file) throws IOException, MalformedWavFileException {

		super(name);

		addPrimaryOutput(name + "_output_" + outputs.size());

		while (outputs.size() < Settings.INSTANCE.getChannelCount()) {
			addSecondaryOutput(name + "_output_" + outputs.size());
		}

		wavFile = new WavFile(file);
		channelCount = wavFile.getChannelCount();
		offset = 0;

		start();
	}

	@Override
	public int compute() throws InterruptedException {

		var bufferSizeInFrames = Settings.INSTANCE.getChunkSize();
		var samples = new float[channelCount][bufferSizeInFrames];

		try {

			var count = wavFile.read(samples, offset);

			if (count <= 0) {
				offset = 0;
			} else {
				offset += count;
			}

		} catch (IOException cause) {

			throw new RuntimeException(cause);
		}

		for (var channelIndex = 0; channelIndex < channelCount; channelIndex++) {
			outputs.get(channelIndex).write(samples[channelIndex]);
		}

		return bufferSizeInFrames;
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
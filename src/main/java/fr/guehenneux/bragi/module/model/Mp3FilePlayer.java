package fr.guehenneux.bragi.module.model;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import fr.guehenneux.bragi.Settings;
import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.DecoderException;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.SampleBuffer;

import static java.nio.file.Files.newInputStream;

/**
 * @author Jonathan Guéhenneux
 */
public class Mp3FilePlayer extends Module implements Player {

	private Bitstream bitStream;
	private Decoder decoder;
	private int channelCount;

	/**
	 * @param name
	 * @param path path of MP3 file to play
	 * @throws FileNotFoundException
	 */
	public Mp3FilePlayer(String name, Path path) throws IOException {

		super(name);

		while (outputs.size() < Settings.INSTANCE.getChannels()) {
			addOutput(name + "_output_" + outputs.size());
		}

		var inputStream = new BufferedInputStream(newInputStream(path));
		bitStream = new Bitstream(inputStream);
		decoder = new Decoder();

		start();
	}

	@Override
	public void compute() throws InterruptedException {

		try {

			var header = bitStream.readFrame();

			if (header != null) {

				var sampleBuffer = (SampleBuffer) decoder.decodeFrame(header, bitStream);

				bitStream.closeFrame();

				var samples = split(sampleBuffer);

				for (int channelIndex = 0; channelIndex < channelCount; channelIndex++) {
					outputs.get(channelIndex).write(samples[channelIndex]);
				}
			}

		} catch (BitstreamException | DecoderException cause) {

			throw new RuntimeException(cause);
		}
	}

	/**
	 * @param sampleBuffer
	 */
	private float[][] split(SampleBuffer sampleBuffer) {

		channelCount = sampleBuffer.getChannelCount();

		var frameCount = sampleBuffer.getBufferLength() / channelCount;
		var buffer = sampleBuffer.getBuffer();
		var samples = new float[channelCount][frameCount];

		var sampleIndex = 0;

		for (int frameIndex = 0; frameIndex < frameCount; frameIndex++) {

			for (int channelIndex = 0; channelIndex < channelCount; channelIndex++) {

				var sample = buffer[sampleIndex++];
				samples[channelIndex][frameIndex] = (float) sample / Short.MAX_VALUE;
			}
		}

		return samples;
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
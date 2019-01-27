package fr.guehenneux.bragi.module;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import fr.guehenneux.bragi.Settings;
import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.DecoderException;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.SampleBuffer;

/**
 * @author Jonathan Guéhenneux
 */
public class Mp3FilePlayer extends Module implements Player {

	private Bitstream bitStream;
	private Header header;
	private SampleBuffer sampleBuffer;
	private Decoder decoder;
	private int channelCount;
	private int frameCount;

	/**
	 * @param name
	 * @param file
	 * @throws FileNotFoundException
	 */
	public Mp3FilePlayer(String name, File file) throws FileNotFoundException {

		super(name);

		while (outputs.size() < Settings.INSTANCE.getChannels()) {
			addOutput(name + "_output_" + outputs.size());
		}

		FileInputStream fileInputStream = new FileInputStream(file);
		InputStream inputStream = new BufferedInputStream(fileInputStream);
		bitStream = new Bitstream(inputStream);
		decoder = new Decoder();

		start();
	}

	@Override
	public void compute() throws InterruptedException {

		try {

			header = bitStream.readFrame();

			if (header != null) {

				sampleBuffer = (SampleBuffer) decoder.decodeFrame(header, bitStream);

				bitStream.closeFrame();

				float[][] samples = split(sampleBuffer);

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
	private final float[][] split(SampleBuffer sampleBuffer) {

		channelCount = sampleBuffer.getChannelCount();
		frameCount = sampleBuffer.getBufferLength() / channelCount;
		short[] buffer = sampleBuffer.getBuffer();

		float[][] samples = new float[channelCount][frameCount];

		int frameIndex, channelIndex, sampleIndex = 0;
		short sampleShort;

		for (frameIndex = 0; frameIndex < frameCount; frameIndex++) {

			for (channelIndex = 0; channelIndex < channelCount; channelIndex++) {

				sampleShort = buffer[sampleIndex++];
				samples[channelIndex][frameIndex] = (float) sampleShort / Short.MAX_VALUE;
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
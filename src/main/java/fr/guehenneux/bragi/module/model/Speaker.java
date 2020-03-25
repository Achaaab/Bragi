package fr.guehenneux.bragi.module.model;

import fr.guehenneux.bragi.Normalizer;
import fr.guehenneux.bragi.Settings;
import org.slf4j.Logger;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import static java.lang.Math.round;
import static javax.sound.sampled.AudioSystem.getLine;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Jonathan Guéhenneux
 */
public class Speaker extends Module {

	private static final Logger LOGGER = getLogger(Speaker.class);

	private static final int ONE_BYTE_MIN_VALUE = 0xFF_FF_FF_A0;
	private static final int ONE_BYTE_MAX_VALUE = 0x00_00_00_7F;

	private static final int TWO_BYTES_MIN_VALUE = 0xFF_FF_A0_00;
	private static final int TWO_BYTES_MAX_VALUE = 0x00_00_7F_FF;

	private static final int THREE_BYTES_MIN_VALUE = 0xFF_A0_00_00;
	private static final int THREE_BYTES_MAX_VALUE = 0x00_7F_FF_FF;

	private static final Normalizer ONE_BYTE_NORMALIZER = new Normalizer(
			Settings.INSTANCE.getMinimalVoltage(), Settings.INSTANCE.getMaximalVoltage(),
			ONE_BYTE_MIN_VALUE, ONE_BYTE_MAX_VALUE
	);

	private static final Normalizer TWO_BYTES_NORMALIZER = new Normalizer(
			Settings.INSTANCE.getMinimalVoltage(), Settings.INSTANCE.getMaximalVoltage(),
			TWO_BYTES_MIN_VALUE, TWO_BYTES_MAX_VALUE
	);

	private static final Normalizer THREE_BYTES_NORMALIZER = new Normalizer(
			Settings.INSTANCE.getMinimalVoltage(), Settings.INSTANCE.getMaximalVoltage(),
			THREE_BYTES_MIN_VALUE, THREE_BYTES_MAX_VALUE
	);

	private SourceDataLine line;

	/**
	 * @param name name of the speaker to create
	 * @throws LineUnavailableException if no source data line is available
	 */
	public Speaker(String name) throws LineUnavailableException {

		super(name);

		addPrimaryInput(name + "_input_" + inputs.size());

		while (inputs.size() < Settings.INSTANCE.getChannelCount()) {
			addSecondaryInput(name + "_input_" + inputs.size());
		}

		var format = new AudioFormat(
				Settings.INSTANCE.getFrameRate(),
				Settings.INSTANCE.getSampleSize() * 8,
				Settings.INSTANCE.getChannelCount(),
				true, true);

		var info = new Info(SourceDataLine.class, format);

		line = (SourceDataLine) getLine(info);
		line.open(format, Settings.INSTANCE.getByteRate() / 20);
		line.start();

		start();
	}

	/**
	 * Check if the line buffer is not empty.
	 * If it is empty, that means it is not written fast enough and sound will not be continuous.
	 */
	private void checkLineBufferHealth() {

		if (line.available() == line.getBufferSize()) {
			LOGGER.warn("buffer underrun");
		}
	}

	@Override
	public int compute() throws InterruptedException {

		var channelCount = Settings.INSTANCE.getChannelCount();

		var samples = new float[channelCount][];

		for (var channelIndex = 0; channelIndex < channelCount; channelIndex++) {
			samples[channelIndex] = inputs.get(channelIndex).read();
		}

		var data = mix(samples);

		checkLineBufferHealth();

		line.write(data, 0, data.length);

		return samples[0].length;
	}

	/**
	 * @param samples samples to mix
	 * @return mixed samples
	 */
	private static byte[] mix(float[][] samples) {

		var sampleSize = Settings.INSTANCE.getSampleSize();
		var channelCount = samples.length;
		var frameCount = samples[0].length;

		var mix = new byte[frameCount * channelCount * sampleSize];

		var byteIndex = 0;

		byte b0;
		byte b1;
		byte b2;

		for (var frameIndex = 0; frameIndex < frameCount; frameIndex++) {

			for (var channelSamples : samples) {

				var sample = channelSamples == null ? 0.0f : channelSamples[frameIndex];

				int normalizedSample;

				switch (sampleSize) {

					case 1:
						normalizedSample = round(ONE_BYTE_NORMALIZER.normalize(sample));
						b0 = (byte) normalizedSample;
						mix[byteIndex++] = b0;
						break;

					case 2:
						normalizedSample = round(TWO_BYTES_NORMALIZER.normalize(sample));
						b0 = (byte) (normalizedSample >> 8);
						b1 = (byte) normalizedSample;
						mix[byteIndex++] = b0;
						mix[byteIndex++] = b1;
						break;

					case 3:
						normalizedSample = round(THREE_BYTES_NORMALIZER.normalize(sample));
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
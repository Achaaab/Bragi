package com.github.achaaab.bragi.core.configuration;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.nio.ByteOrder.BIG_ENDIAN;
import static java.nio.ByteOrder.LITTLE_ENDIAN;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static javax.sound.sampled.AudioSystem.getMixerInfo;

/**
 * line configuration
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.8
 */
public class LineConfiguration {

	private static final Integer[] SAMPLE_RATES = {
			8000, 11025, 16000, 22050, 32000, 44100, 48000, 88200, 96000, 176400, 192000 };

	/**
	 * @return available mixers
	 */
	private static Stream<Mixer> getAvailableMixers() {
		return stream(getMixerInfo()).map(AudioSystem::getMixer);
	}

	/**
	 * @param format audio format
	 * @return byte order of the given audio format
	 */
	private static ByteOrder getByteOrder(AudioFormat format) {
		return format.isBigEndian() ? BIG_ENDIAN : LITTLE_ENDIAN;
	}

	private final Function<Mixer, Line.Info[]> suitableLineFunction;

	private Mixer mixer;
	private List<AudioFormat> formats;
	private int channelCount;
	private int sampleRate;
	private int sampleSize;
	private Encoding encoding;
	private ByteOrder byteOrder;

	/**
	 * Creates a new line configuration.
	 *
	 * @param suitableLineFunction function that extracts suitable line information from a mixer
	 */
	public LineConfiguration(Function<Mixer, Line.Info[]> suitableLineFunction) {

		this.suitableLineFunction = suitableLineFunction;

		var mixers = mixers();

		if (mixers.length == 0) {
			throw new ConfigurationException("no suitable mixer");
		}

		// TODO find the default mixer
		setMixer(mixers[0]);

		sampleRate = sampleRates()[0];
	}

	/**
	 * @return suitable mixers
	 */
	public Mixer[] mixers() {
		return getAvailableMixers().filter(this::isSuitable).toArray(Mixer[]::new);
	}


	/**
	 * @return available sample rates
	 */
	public Integer[] sampleRates() {
		return SAMPLE_RATES;
	}

	/**
	 * @return suitable channel counts
	 */
	public Integer[] channelCounts() {

		return formats.stream().
				map(AudioFormat::getChannels).distinct().sorted().
				toArray(Integer[]::new);
	}

	/**
	 * @return suitable encodings
	 * @see Encoding#PCM_SIGNED
	 * @see Encoding#PCM_UNSIGNED
	 * @see Encoding#PCM_FLOAT
	 */
	public Encoding[] encodings() {

		return formats.stream().
				map(AudioFormat::getEncoding).distinct().
				toArray(Encoding[]::new);
	}

	/**
	 * @return suitable byte orders
	 * @see ByteOrder#LITTLE_ENDIAN
	 * @see ByteOrder#BIG_ENDIAN
	 */
	public ByteOrder[] byteOrders() {

		return formats.stream().
				map(LineConfiguration::getByteOrder).distinct().
				toArray(ByteOrder[]::new);
	}

	/**
	 * @return suitable sample sizes in bits (b)
	 */
	public Integer[] sampleSizes() {

		return formats.stream().
				map(AudioFormat::getSampleSizeInBits).distinct().sorted().
				toArray(Integer[]::new);
	}

	/**
	 * @return selected mixer
	 */
	public Mixer getMixer() {
		return mixer;
	}

	/**
	 * @param mixer mixer to set
	 */
	public void setMixer(Mixer mixer) {

		this.mixer = mixer;

		formats = getFormats(mixer);

		channelCount = channelCounts()[0];
		sampleSize = sampleSizes()[0];
		encoding = encodings()[0];
		byteOrder = byteOrders()[0];
	}

	/**
	 * @return current audio format
	 */
	public AudioFormat format() {

		var frameSize = channelCount * sampleSize / 8;
		var bigEndian = byteOrder == BIG_ENDIAN;

		return new AudioFormat(
				encoding,
				sampleRate,
				sampleSize,
				channelCount,
				frameSize,
				sampleRate,
				bigEndian
		);
	}

	/**
	 * @return selected number of channels
	 */
	public int getChannelCount() {
		return channelCount;
	}

	/**
	 * @param channelCount number of channels to set
	 */
	public void setChannelCount(int channelCount) {
		this.channelCount = channelCount;
	}

	/**
	 * @return selected sample rate in hertz (Hz)
	 */
	public int getSampleRate() {
		return sampleRate;
	}

	/**
	 * @param sampleRate sample rate to set in hertz (Hz)
	 */
	public void setSampleRate(int sampleRate) {
		this.sampleRate = sampleRate;
	}

	/**
	 * @return selected sample size in bits (b)
	 */
	public int getSampleSize() {
		return sampleSize;
	}

	/**
	 * @param sampleSize size of the samples in bits
	 */
	public void setSampleSize(int sampleSize) {
		this.sampleSize = sampleSize;
	}

	/**
	 * @return selected encoding
	 * @see Encoding#PCM_SIGNED
	 * @see Encoding#PCM_UNSIGNED
	 * @see Encoding#PCM_FLOAT
	 */
	public Encoding getEncoding() {
		return encoding;
	}

	/**
	 * @param encoding encoding to set
	 * @see Encoding#PCM_SIGNED
	 * @see Encoding#PCM_UNSIGNED
	 * @see Encoding#PCM_FLOAT
	 */
	public void setEncoding(Encoding encoding) {
		this.encoding = encoding;
	}

	/**
	 * @return selected byte order
	 * @see ByteOrder#LITTLE_ENDIAN
	 * @see ByteOrder#BIG_ENDIAN
	 */
	public ByteOrder getByteOrder() {
		return byteOrder;
	}

	/**
	 * @param byteOrder byte order to set
	 * @see ByteOrder#LITTLE_ENDIAN
	 * @see ByteOrder#BIG_ENDIAN
	 */
	public void setByteOrder(ByteOrder byteOrder) {
		this.byteOrder = byteOrder;
	}

	/**
	 * @return copy of this line configuration
	 */
	public LineConfiguration copy() {

		var clone = new LineConfiguration(suitableLineFunction);

		clone.setMixer(mixer);
		clone.setSampleRate(sampleRate);
		clone.setChannelCount(channelCount);
		clone.setSampleSize(sampleSize);
		clone.setEncoding(encoding);
		clone.setByteOrder(byteOrder);

		return clone;
	}

	/**
	 * Copies the given line configuration into this.
	 * We assume they both have the same {@code suitableLineFunction}.
	 *
	 * @param lineConfiguration line configuration to copy
	 */
	public void copy(LineConfiguration lineConfiguration) {

		mixer = lineConfiguration.mixer;
		sampleRate = lineConfiguration.sampleRate;
		channelCount = lineConfiguration.channelCount;
		sampleSize = lineConfiguration.sampleSize;
		encoding = lineConfiguration.encoding;
		byteOrder = lineConfiguration.byteOrder;
	}

	/**
	 * @param mixer mixer
	 * @return whether the given mixer has a suitable line
	 */
	private boolean isSuitable(Mixer mixer) {
		return !getFormats(mixer).isEmpty();
	}

	/**
	 * @param mixer mixer
	 * @return audio formats supported by the suitable lines of the given mixer
	 */
	private List<AudioFormat> getFormats(Mixer mixer) {

		return stream(suitableLineFunction.apply(mixer)).
				filter(DataLine.Info.class::isInstance).
				map(DataLine.Info.class::cast).
				map(DataLine.Info::getFormats).
				flatMap(Arrays::stream).
				collect(toList());
	}
}
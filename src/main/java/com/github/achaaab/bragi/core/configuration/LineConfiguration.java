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

import static com.github.achaaab.bragi.common.ArrayUtils.contains;
import static java.nio.ByteOrder.BIG_ENDIAN;
import static java.nio.ByteOrder.LITTLE_ENDIAN;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED;
import static javax.sound.sampled.AudioSystem.NOT_SPECIFIED;
import static javax.sound.sampled.AudioSystem.getMixerInfo;

/**
 * line configuration
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.8
 */
public class LineConfiguration {

	private static final int PREFERRED_CHANNEL_COUNT = 2;
	private static final int PREFERRED_SAMPLE_RATE = 96_000;
	private static final int PREFERRED_SAMPLE_SIZE = 24;
	private static final Encoding PREFERRED_ENCODING = PCM_SIGNED;
	private static final ByteOrder PREFERRED_BYTE_ORDER = BIG_ENDIAN;

	private static final Integer[] SAMPLE_RATES = {
			8_000,
			11_025,
			16_000,
			22_050,
			32_000,
			44_100,
			48_000,
			88_200,
			96_000,
			176_400,
			192_000 };

	/**
	 * @return available mixers
	 * @since 0.2.0
	 */
	private static Stream<Mixer> getAvailableMixers() {
		return stream(getMixerInfo()).map(AudioSystem::getMixer);
	}

	/**
	 * @param format audio format
	 * @return byte order of the given audio format
	 * @since 0.2.0
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
	 * @since 0.2.0
	 */
	public LineConfiguration(Function<Mixer, Line.Info[]> suitableLineFunction) {

		this.suitableLineFunction = suitableLineFunction;

		var mixers = mixers();

		if (mixers.length == 0) {
			throw new ConfigurationException("no suitable mixer");
		}

		// TODO find the default mixer
		setMixer(mixers[0]);
	}

	/**
	 * @return suitable mixers
	 * @since 0.2.0
	 */
	public Mixer[] mixers() {

		return getAvailableMixers().
				filter(this::isSuitable).
				toArray(Mixer[]::new);
	}

	/**
	 * @return available sample rates
	 * @since 0.2.0
	 */
	public Integer[] sampleRates() {
		return SAMPLE_RATES;
	}

	/**
	 * @return suitable channel counts
	 * @since 0.2.0
	 */
	public Integer[] channelCounts() {

		return formats.stream().
				map(AudioFormat::getChannels).
				distinct().sorted().
				toArray(Integer[]::new);
	}

	/**
	 * @return suitable encodings
	 * @see Encoding#PCM_SIGNED
	 * @see Encoding#PCM_UNSIGNED
	 * @see Encoding#PCM_FLOAT
	 * @since 0.2.0
	 */
	public Encoding[] encodings() {

		return formats.stream().
				map(AudioFormat::getEncoding).
				distinct().
				toArray(Encoding[]::new);
	}

	/**
	 * @return suitable byte orders
	 * @see ByteOrder#LITTLE_ENDIAN
	 * @see ByteOrder#BIG_ENDIAN
	 * @since 0.2.0
	 */
	public ByteOrder[] byteOrders() {

		return formats.stream().
				map(LineConfiguration::getByteOrder).
				distinct().
				toArray(ByteOrder[]::new);
	}

	/**
	 * @return suitable sample sizes in bits (b)
	 * @since 0.2.0
	 */
	public Integer[] sampleSizes() {

		return formats.stream().
				map(AudioFormat::getSampleSizeInBits).
				distinct().sorted().
				toArray(Integer[]::new);
	}

	/**
	 * @return selected mixer
	 * @since 0.2.0
	 */
	public Mixer getMixer() {
		return mixer;
	}

	/**
	 * Sets the mixer to use and presets audio format based on preferences and mixer supported formats.
	 *
	 * @param mixer mixer to set
	 * @since 0.2.0
	 */
	public void setMixer(Mixer mixer) {

		this.mixer = mixer;

		formats = getFormats(mixer);

		// set channel count
		var channelCounts = channelCounts();
		channelCount = channelCounts[0];

		if (channelCount == NOT_SPECIFIED || contains(channelCounts, PREFERRED_CHANNEL_COUNT)) {
			channelCount = PREFERRED_CHANNEL_COUNT;
		}

		formats = formats.stream().
				filter(format -> format.getChannels() == channelCount || format.getChannels() == NOT_SPECIFIED).
				toList();

		// set sample size
		var sampleSizes = sampleSizes();
		sampleSize = contains(sampleSizes, PREFERRED_SAMPLE_SIZE) ?
				PREFERRED_SAMPLE_SIZE :
				sampleSizes[0];

		formats = formats.stream().
				filter(format -> format.getSampleSizeInBits() == sampleSize).
				toList();

		// set sample rate
		sampleRate = PREFERRED_SAMPLE_RATE;

		// set encoding
		var encodings = encodings();
		encoding = contains(encodings, PREFERRED_ENCODING) ?
				PREFERRED_ENCODING :
				encodings[0];

		formats = formats.stream().
				filter(format -> format.getEncoding() == encoding).
				toList();

		// set byte order
		var byteOrders = byteOrders();
		byteOrder = contains(byteOrders, PREFERRED_BYTE_ORDER) ?
				PREFERRED_BYTE_ORDER :
				byteOrders[0];
	}

	/**
	 * @return current audio format
	 * @since 0.2.0
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
				bigEndian);
	}

	/**
	 * @return selected number of channels
	 * @since 0.2.0
	 */
	public int getChannelCount() {
		return channelCount;
	}

	/**
	 * @param channelCount number of channels to set
	 * @since 0.2.0
	 */
	public void setChannelCount(int channelCount) {
		this.channelCount = channelCount;
	}

	/**
	 * @return selected sample rate in hertz (Hz)
	 * @since 0.2.0
	 */
	public int getSampleRate() {
		return sampleRate;
	}

	/**
	 * @param sampleRate sample rate to set in hertz (Hz)
	 * @since 0.2.0
	 */
	public void setSampleRate(int sampleRate) {
		this.sampleRate = sampleRate;
	}

	/**
	 * @return selected sample size in bits (b)
	 * @since 0.2.0
	 */
	public int getSampleSize() {
		return sampleSize;
	}

	/**
	 * @param sampleSize size of the samples in bits
	 * @since 0.2.0
	 */
	public void setSampleSize(int sampleSize) {
		this.sampleSize = sampleSize;
	}

	/**
	 * @return selected encoding
	 * @see Encoding#PCM_SIGNED
	 * @see Encoding#PCM_UNSIGNED
	 * @see Encoding#PCM_FLOAT
	 * @since 0.2.0
	 */
	public Encoding getEncoding() {
		return encoding;
	}

	/**
	 * @param encoding encoding to set
	 * @see Encoding#PCM_SIGNED
	 * @see Encoding#PCM_UNSIGNED
	 * @see Encoding#PCM_FLOAT
	 * @since 0.2.0
	 */
	public void setEncoding(Encoding encoding) {
		this.encoding = encoding;
	}

	/**
	 * @return selected byte order
	 * @see ByteOrder#LITTLE_ENDIAN
	 * @see ByteOrder#BIG_ENDIAN
	 * @since 0.2.0
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
	 * @since 0.2.0
	 */
	public LineConfiguration copy() {

		var copy = new LineConfiguration(suitableLineFunction);

		copy.setMixer(mixer);
		copy.setSampleRate(sampleRate);
		copy.setChannelCount(channelCount);
		copy.setSampleSize(sampleSize);
		copy.setEncoding(encoding);
		copy.setByteOrder(byteOrder);

		return copy;
	}

	/**
	 * Copies the given line configuration into this.
	 * We assume they both have the same {@code suitableLineFunction}.
	 *
	 * @param lineConfiguration line configuration to copy
	 * @since 0.2.0
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
	 * @since 0.2.0
	 */
	private boolean isSuitable(Mixer mixer) {
		return !getFormats(mixer).isEmpty();
	}

	/**
	 * @param mixer mixer
	 * @return audio formats supported by the suitable lines of the given mixer
	 * @since 0.2.0
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

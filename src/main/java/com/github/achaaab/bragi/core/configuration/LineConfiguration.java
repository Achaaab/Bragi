package com.github.achaaab.bragi.core.configuration;

import com.github.achaaab.bragi.gui.configuration.LineConfigurationView;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;
import java.lang.reflect.InvocationTargetException;
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
import static javax.swing.SwingUtilities.invokeAndWait;

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

	private final Function<Mixer, Line.Info[]> suitableLinesFunction;

	private Mixer mixer;
	private List<AudioFormat> formats;
	private int channelCount;
	private int sampleRate;
	private int sampleSize;
	private Encoding encoding;
	private ByteOrder byteOrder;

	private LineConfigurationView view;

	/**
	 * Creates a new line configuration.
	 *
	 * @param suitableLinesFunction function that extracts suitable line information from a mixer
	 */
	public LineConfiguration(Function<Mixer, Line.Info[]> suitableLinesFunction) {

		this.suitableLinesFunction = suitableLinesFunction;

		var mixers = getMixers();

		if (mixers.length == 0) {
			throw new ConfigurationException("no suitable mixer");
		}

		// TODO find the default mixer
		setMixer(mixers[0]);

		sampleRate = getSampleRates()[0];
		channelCount = getChannelCounts()[0];
		sampleSize = getSampleSizes()[0];
		encoding = getEncodings()[0];
		byteOrder = getByteOrders()[0];

		try {
			invokeAndWait(() -> view = new LineConfigurationView(this));
		} catch (InterruptedException | InvocationTargetException cause) {
			throw new ConfigurationException(cause);
		}
	}

	/**
	 * @return view of this line configuration
	 */
	public LineConfigurationView view() {
		return view;
	}

	/**
	 * @return suitable mixers
	 */
	public Mixer[] getMixers() {
		return getAvailableMixers().filter(this::isSuitable).toArray(Mixer[]::new);
	}


	/**
	 * @return available sample rates
	 */
	public Integer[] getSampleRates() {
		return SAMPLE_RATES;
	}

	/**
	 * @return suitable channel counts
	 */
	public Integer[] getChannelCounts() {

		return formats.stream().
				map(AudioFormat::getChannels).distinct().
				toArray(Integer[]::new);
	}

	/**
	 * @return suitable encodings
	 * @see Encoding#PCM_SIGNED
	 * @see Encoding#PCM_UNSIGNED
	 * @see Encoding#PCM_FLOAT
	 */
	public Encoding[] getEncodings() {

		return formats.stream().
				map(AudioFormat::getEncoding).distinct().
				toArray(Encoding[]::new);
	}

	/**
	 * @return suitable byte orders
	 * @see ByteOrder#LITTLE_ENDIAN
	 * @see ByteOrder#BIG_ENDIAN
	 */
	public ByteOrder[] getByteOrders() {

		return formats.stream().
				map(LineConfiguration::getByteOrder).distinct().
				toArray(ByteOrder[]::new);
	}

	/**
	 * @return suitable sample sizes in bits (b)
	 */
	public Integer[] getSampleSizes() {

		return formats.stream().
				map(AudioFormat::getSampleSizeInBits).distinct().
				toArray(Integer[]::new);
	}

	/**
	 * @param mixer mixer to set
	 */
	public void setMixer(Mixer mixer) {

		this.mixer = mixer;

		formats = getFormats(mixer);
	}

	/**
	 * @param sampleSize size of the samples in bits
	 */
	public void setSampleSize(int sampleSize) {
		this.sampleSize = sampleSize;
	}

	/**
	 * @return selected mixer
	 */
	public Mixer getMixer() {
		return mixer;
	}

	/**
	 * @return selected channel count
	 */
	public int getChannelCount() {
		return channelCount;
	}

	/**
	 * @return selected sample rate in hertz (Hz)
	 */
	public int getSampleRate() {
		return sampleRate;
	}

	/**
	 * @return selected sample size in bits (b)
	 */
	public int getSampleSize() {
		return sampleSize;
	}

	/**
	 * @return selected encoding
	 */
	public Encoding getEncoding() {
		return encoding;
	}

	/**
	 * @return selected byte order
	 */
	public ByteOrder getByteOrder() {
		return byteOrder;
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

		return stream(suitableLinesFunction.apply(mixer)).
				filter(DataLine.Info.class::isInstance).
				map(DataLine.Info.class::cast).
				map(DataLine.Info::getFormats).
				flatMap(Arrays::stream).
				collect(toList());
	}

	/**
	 * @param format audio format to check
	 * @return whether the given audio format matches the selected criteria
	 */
	private boolean isSuitable(AudioFormat format) {

		var suitable = format.getChannels() == channelCount;
		suitable &= format.getSampleSizeInBits() == sampleSize;
		suitable &= getByteOrder(format) == byteOrder;
		suitable &= format.getEncoding() == encoding;

		return suitable;
	}
}
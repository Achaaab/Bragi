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

	protected static final Integer[] SAMPLE_RATES = {
			8000, 11025, 16000, 22050, 32000, 44100, 48000, 88200, 96000, 176400, 192000 };

	/**
	 * @return available mixers
	 */
	protected static Stream<Mixer> getAvailableMixers() {
		return stream(getMixerInfo()).map(AudioSystem::getMixer);
	}

	private final Function<Mixer, Line.Info[]> suitableLinesFunction;

	protected Mixer mixer;
	protected List<AudioFormat> formats;
	protected int channelCount;
	protected int sampleRate;
	protected int sampleSize;
	protected Encoding encoding;
	protected ByteOrder byteOrder;

	private LineConfigurationView view;

	/**
	 * Creates a new line configuration.
	 *
	 * @param suitableLinesFunction function that extracts suitable line information from a mixer
	 */
	public LineConfiguration(Function<Mixer, Line.Info[]> suitableLinesFunction) {

		this.suitableLinesFunction = suitableLinesFunction;

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
	 * @param mixer mixer
	 * @return whether the given mixer has a suitable line
	 */
	public boolean isSuitable(Mixer mixer) {
		return !getFormats(mixer).isEmpty();
	}

	/**
	 * @return available sample rates
	 */
	public Integer[] getSampleRates() {
		return SAMPLE_RATES;
	}

	/**
	 * @param mixer mixer to set
	 */
	public void setMixer(Mixer mixer) {
		this.mixer = mixer;
		formats = getFormats(mixer);
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
}
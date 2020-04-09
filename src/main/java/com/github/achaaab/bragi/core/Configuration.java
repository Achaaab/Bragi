package com.github.achaaab.bragi.core;

import com.github.achaaab.bragi.gui.ConfigurationView;
import org.slf4j.Logger;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static javax.sound.sampled.AudioSystem.getMixerInfo;
import static javax.swing.SwingUtilities.invokeLater;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Jonathan Guéhenneux
 * @since 0.1.8
 */
public class Configuration {

	private static final Logger LOGGER = getLogger(Configuration.class);

	public static final Mixer[] INPUT_MIXERS = getAvailableInputMixers();
	public static final Mixer[] OUTPUT_MIXERS = getAvailableOutputMixers();

	public static final Integer[] SAMPLE_RATES = {
			8000, 11025, 16000, 22050, 32000, 44100, 48000, 88200, 96000, 176400, 192000 };

	/**
	 * @return available mixers
	 */
	private static Stream<Mixer> getAvailableMixers() {
		return stream(getMixerInfo()).map(AudioSystem::getMixer);
	}

	/**
	 * @return available mixers with at least 1 target data line (input line)
	 */
	private static Mixer[] getAvailableInputMixers() {
		return getAvailableMixers().filter(Configuration::hasInputLine).toArray(Mixer[]::new);
	}

	/**
	 * @return available mixers with at least 1 source data line (output line)
	 */
	private static Mixer[] getAvailableOutputMixers() {
		return getAvailableMixers().filter(Configuration::hasOutputLine).toArray(Mixer[]::new);
	}

	/**
	 * @param mixer mixer
	 * @return whether the given mixer has at least 1 target data line (input line)
	 */
	private static boolean hasInputLine(Mixer mixer) {
		return !getAudioFormats(mixer.getTargetLineInfo()).isEmpty();
	}

	/**
	 * @param mixer mixer
	 * @return whether the given mixer has at least 1 source data line (output line)
	 */
	private static boolean hasOutputLine(Mixer mixer) {
		return !getAudioFormats(mixer.getSourceLineInfo()).isEmpty();
	}

	/**
	 * @param lineInformationArray array of line information
	 * @return audio formats supported by the given line information
	 */
	private static List<AudioFormat> getAudioFormats(Line.Info[] lineInformationArray) {

		return stream(lineInformationArray).
				filter(DataLine.Info.class::isInstance).
				map(DataLine.Info.class::cast).
				map(DataLine.Info::getFormats).
				flatMap(Arrays::stream).
				collect(toList());
	}

	private Mixer inputMixer;
	private Mixer outputMixer;

	private ByteOrder byteOrder;
	private int sampleRate;
	private int sampleSize;
	private boolean signed;
	private int channelCount;

	private AudioFormat inputFormat;
	private AudioFormat outputFormat;

	private ConfigurationView view;

	/**
	 * Creates a new synthesizer.
	 */
	public Configuration() {
		invokeLater(() -> view = new ConfigurationView(this));
	}

	/**
	 * Sets the mixer for target data lines.
	 *
	 * @param inputMixer input mixer to set
	 */
	public void setInputMixer(Mixer inputMixer) {
		this.inputMixer = inputMixer;
	}

	/**
	 * Sets the mixer for source data lines.
	 *
	 * @param outputMixer output mixer to set
	 */
	public void setOutputMixer(Mixer outputMixer) {
		this.outputMixer = outputMixer;
	}

	/**
	 * @return formats supported by the selected input mixer
	 */
	public List<AudioFormat> getInputFormats() {
		return getAudioFormats(inputMixer.getTargetLineInfo());
	}

	/**
	 * @return formats supported by the selected output mixer
	 */
	public List<AudioFormat> getOutputFormats() {
		return getAudioFormats(outputMixer.getSourceLineInfo());
	}

	/**
	 * Sets the audio format for input.
	 *
	 * @param inputFormat input format to set
	 */
	public void setInputFormat(AudioFormat inputFormat) {
		this.inputFormat = inputFormat;
	}

	/**
	 * Sets the audio format for output.
	 *
	 * @param outputFormat input format to set
	 */
	public void setOutputFormat(AudioFormat outputFormat) {
		this.inputFormat = outputFormat;
	}
}
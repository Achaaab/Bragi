package com.github.achaaab.bragi;

import com.github.achaaab.bragi.common.Settings;
import com.github.achaaab.bragi.core.Synthesizer;
import com.github.achaaab.bragi.core.module.Module;
import com.github.achaaab.bragi.core.module.consumer.Oscilloscope;
import com.github.achaaab.bragi.core.module.consumer.Speaker;
import com.github.achaaab.bragi.core.module.consumer.SpectrumAnalyzer;
import com.github.achaaab.bragi.core.module.player.FlacPlayer;
import com.github.achaaab.bragi.core.module.player.Mp3Player;
import com.github.achaaab.bragi.core.module.player.WavPlayer;
import com.github.achaaab.bragi.core.module.producer.Adsr;
import com.github.achaaab.bragi.core.module.producer.Dcg;
import com.github.achaaab.bragi.core.module.producer.Keyboard;
import com.github.achaaab.bragi.core.module.producer.Lfo;
import com.github.achaaab.bragi.core.module.producer.Microphone;
import com.github.achaaab.bragi.core.module.producer.PinkNoiseGenerator;
import com.github.achaaab.bragi.core.module.producer.Theremin;
import com.github.achaaab.bragi.core.module.producer.Vco;
import com.github.achaaab.bragi.core.module.producer.WhiteNoiseGenerator;
import com.github.achaaab.bragi.core.module.transformer.HighPassVcf;
import com.github.achaaab.bragi.core.module.transformer.LowPassVcf;
import com.github.achaaab.bragi.core.module.transformer.Mixer;
import com.github.achaaab.bragi.core.module.transformer.Vca;
import org.slf4j.Logger;

import java.nio.file.Path;

import static com.github.achaaab.bragi.ResourceUtils.getPath;
import static com.github.achaaab.bragi.core.module.producer.wave.Waveform.SAWTOOTH_TRIANGULAR;
import static com.github.achaaab.bragi.core.module.producer.wave.Waveform.TRIANGLE;
import static javax.swing.SwingUtilities.invokeLater;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * test bank
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.9
 */
public class Test {

	private static final Logger LOGGER = getLogger(Test.class);

	private static final Path TEST_MP3_PATH = getPath("test_44100.mp3");
	private static final Path TEST_WAV_PATH = getPath("test_44100.wav");
	private static final Path TEST_FLAC_PATH = getPath("test_96000.flac");
	private static final Path TEST_MP3_32000_PATH = getPath("test_32000.mp3");
	private static final Path TEST_WAV_8000_PATH = getPath("test_8000.wav");

	/**
	 * @param arguments none
	 * @since 0.0.9
	 */
	public static void main(String... arguments) {
		testTremolo();
	}

	/**
	 * Tests interpolation in {@link WavPlayer}.
	 *
	 * @since 0.1.7
	 */
	public static void testWavPlayerInterpolation() {

		var player = new WavPlayer(TEST_WAV_8000_PATH);
		var speaker = new Speaker();

		player.connect(speaker);

		visualizeOutputs(player);
		createSynthesizer(player);
	}

	/**
	 * Tests interpolation in {@link Mp3Player}.
	 *
	 * @since 0.1.7
	 */
	public static void testMp3PlayerInterpolation() {

		var player = new Mp3Player(TEST_MP3_32000_PATH);
		var speaker = new Speaker();

		player.connect(speaker);

		visualizeOutputs(player);
		createSynthesizer(player);
	}

	/**
	 * Tests a band-pass filter with one low-pass VCF and one high-pass VCF.
	 *
	 * @since 0.1.6
	 */
	public static void testBandPassFilter() {

		var noise = new WhiteNoiseGenerator();
		var low = new LowPassVcf();
		var high = new HighPassVcf();
		var speaker = new Speaker();
		var spectrum = new SpectrumAnalyzer();

		noise.connect(low);
		low.connect(high);
		speaker.connectInputs(high, high);
		high.connect(spectrum);

		createSynthesizer(noise);
	}

	/**
	 * Tests a piano-like sound (currently far from it).
	 *
	 * @since 0.1.6
	 */
	public static void testPiano() {

		// main chain
		var keyboard = new Keyboard();
		var vcoHigh = new Vco("high");
		var vcoLow = new Vco("low");
		var mixer = new Mixer();
		var filter = new LowPassVcf();
		var envelope = new Vca();
		var speaker = new Speaker();

		keyboard.connect(vcoHigh, vcoLow);
		keyboard.connect(filter.modulation());

		mixer.connectInputs(vcoHigh, vcoLow);
		mixer.connect(filter);
		filter.connect(envelope);

		speaker.connectInputs(envelope, envelope);

		var adsr = new Adsr();
		keyboard.gate().connect(adsr.gate());
		adsr.connect(envelope.gain());

		vcoHigh.setWaveform(TRIANGLE);
		vcoLow.setWaveform(SAWTOOTH_TRIANGULAR);
		vcoHigh.setOctave(0);
		vcoLow.setOctave(-1);
		adsr.setAttack(1000);
		adsr.setRelease(100);
		adsr.setSustain(-0.2f);
		adsr.setRelease(3.0f);
		filter.setCutoffFrequency(10240.0f);

		visualizeOutputs(envelope);
		createSynthesizer(keyboard);
	}

	/**
	 * Tests filter "self" oscillation.
	 * <p>
	 * Due to the architecture of Bragi, the filter needs an input to produce an output.
	 * We could use a DCG with 0 volt, but it would not trigger the oscillation.
	 * Like in an analog synthesizer, we need a tiny noise to trigger the oscillation.
	 *
	 * @since 0.1.6
	 */
	public static void testSelfOscillatingFilter() {

		var noise = new WhiteNoiseGenerator();
		var vca = new Vca();
		var filter = new LowPassVcf();
		var speaker = new Speaker();

		// very low noise (-200 dB) is enough to trigger "self" oscillation of filter
		vca.setInitialGain(-200);

		// maximum emphasis
		filter.setEmphasis(1.0f);

		noise.connect(vca);
		vca.connect(filter);
		speaker.connectInputs(filter, filter);

		visualizeOutputs(filter);
		createSynthesizer(noise);
	}

	/**
	 * Tests tremolo effect.
	 *
	 * @since 0.1.3
	 */
	public static void testTremolo() {

		var adsr = new Adsr();
		var keyboard = new Keyboard();
		var vco = new Vco();
		var vcaEnvelope = new Vca("vca_envelope");
		var speaker = new Speaker();
		var lfo = new Lfo();
		var vcaTremolo = new Vca("vca_tremolo");

		// main chain
		keyboard.connect(vco);
		vco.connect(vcaEnvelope);
		vcaEnvelope.connect(vcaTremolo);
		speaker.connectInputs(vcaTremolo, vcaTremolo);

		// ADSR + tremolo
		keyboard.gate().connect(adsr.gate());
		adsr.connect(vcaEnvelope.gain());
		lfo.connect(vcaTremolo.gain());

		// some tuning
		vco.setWaveform(SAWTOOTH_TRIANGULAR);
		adsr.setAttack(1000.0);
		adsr.setRelease(2.0);

		visualizeOutputs(vcaTremolo);
		createSynthesizer(keyboard);
	}

	/**
	 * Tests the {@link FlacPlayer} module.
	 *
	 * @since 0.1.7
	 */
	public static void testFlacPlayer() {

		var player = new FlacPlayer(TEST_FLAC_PATH);
		var speaker = new Speaker();

		player.connectOutputs(speaker);

		visualizeOutputs(player);
		createSynthesizer(player);
	}

	/**
	 * Tests the {@link Dcg} module.
	 *
	 * @since 0.1.6
	 */
	public static void testDcg() {

		var dcg = new Dcg();
		var oscilloscope = new Oscilloscope();

		dcg.setComputingFrameRate(Settings.INSTANCE.frameRate());

		dcg.connect(oscilloscope);
		createSynthesizer(dcg);
	}

	/**
	 * Tests the {@link Adsr} module.
	 *
	 * @since 0.1.1
	 */
	public static void testAdsr() {

		var adsr = new Adsr();
		var keyboard = new Keyboard();
		var vco = new Vco();
		var vca = new Vca();
		var speaker = new Speaker();

		keyboard.connect(vco);
		vco.connect(vca);
		speaker.connectInputs(vca, vca);
		keyboard.gate().connect(adsr.gate());
		adsr.connect(vca.gain());

		createSynthesizer(keyboard);
	}

	/**
	 * Tests the {@link WavPlayer} module.
	 * TODO Add a module to change the sample rate.
	 *
	 * @since 0.1.0
	 */
	public static void testWavPlayer() {

		var player = new WavPlayer(TEST_WAV_PATH);
		var speaker = new Speaker();

		player.connectOutputs(speaker);

		visualizeOutputs(player);
		createSynthesizer(player);
	}

	/**
	 * Tests the {@link LowPassVcf} module.
	 *
	 * @since 0.0.9
	 */
	public static void testLowPassVcf() {

		var noise = new WhiteNoiseGenerator();
		var filter = new LowPassVcf();
		var spectrum = new SpectrumAnalyzer();
		var speaker = new Speaker();

		noise.connect(filter);
		filter.connect(spectrum);
		speaker.connectInputs(filter, filter);

		createSynthesizer(noise);
	}

	/**
	 * Tests the {@link HighPassVcf} module.
	 *
	 * @since 0.1.6
	 */
	public static void testHighPassVcf() {

		var noise = new WhiteNoiseGenerator();
		var filter = new HighPassVcf();
		var spectrum = new SpectrumAnalyzer();
		var speaker = new Speaker();

		noise.connect(filter);
		filter.connect(spectrum);
		speaker.connectInputs(filter, filter);

		createSynthesizer(noise);
	}

	/**
	 * Tests the {@link PinkNoiseGenerator} module.
	 *
	 * @since 0.0.9
	 */
	public static void testPinkNoiseGenerator() {

		var noise = new PinkNoiseGenerator();
		var speaker = new Speaker();

		speaker.connectInputs(noise, noise);

		visualizeOutputs(noise);
		createSynthesizer(noise);
	}

	/**
	 * Tests the {@link WhiteNoiseGenerator} module.
	 *
	 * @since 0.0.9
	 */
	public static void testWhiteNoiseGenerator() {

		var noise = new WhiteNoiseGenerator();
		var speaker = new Speaker();

		speaker.connectInputs(noise, noise);

		visualizeOutputs(noise);
		createSynthesizer(noise);
	}

	/**
	 * Tests the {@link Lfo} module.
	 *
	 * @since 0.0.9
	 */
	public static void testLfo() {

		var vco = new Vco();
		var speaker = new Speaker();
		var lfo = new Lfo();

		speaker.connectInputs(vco, vco);
		lfo.connect(vco);

		visualizeOutputs(vco);
		createSynthesizer(vco);
	}

	/**
	 * Tests the {@link SpectrumAnalyzer} module.
	 *
	 * @since 0.0.9
	 */
	public static void testSpectrumAnalyzer() {

		var theremin = new Theremin();
		var vco = new Vco();
		var vca = new Vca();
		var spectrum = new SpectrumAnalyzer();

		spectrum.setComputingFrameRate(Settings.INSTANCE.frameRate());

		theremin.connect(vco);
		vco.connect(vca);
		vca.connect(spectrum);
		theremin.volume().connect(vca.gain());

		createSynthesizer(theremin);
	}

	/**
	 * Tests the {@link Keyboard} module.
	 *
	 * @since 0.0.9
	 */
	public static void testKeyboard() {

		var keyboard = new Keyboard();
		var vco = new Vco();
		var speaker = new Speaker();
		var oscilloscope = new Oscilloscope();

		keyboard.connect(vco);
		vco.connect(oscilloscope);
		speaker.connectInputs(vco, vco);

		createSynthesizer(keyboard);
	}

	/**
	 * Tests the {@link Theremin} module.
	 *
	 * @since 0.0.9
	 */
	public static void testTheremin() {

		var theremin = new Theremin();
		var vco = new Vco();
		var vca = new Vca();
		var oscilloscope = new Oscilloscope();

		theremin.setComputingFrameRate(Settings.INSTANCE.frameRate());

		theremin.connect(vco);
		vco.connect(vca);
		vca.connect(oscilloscope);

		theremin.volume().connect(vca.gain());

		createSynthesizer(theremin);
	}

	/**
	 * Tests the {@link Oscilloscope} module.
	 *
	 * @since 0.0.9
	 */
	public static void testOscilloscope() {

		var vco = new Vco();
		var oscilloscope = new Oscilloscope();

		oscilloscope.setComputingFrameRate(Settings.INSTANCE.frameRate());

		vco.connect(oscilloscope);

		createSynthesizer(vco);
	}

	/**
	 * Tests the {@link Microphone} module.
	 *
	 * @since 0.0.9
	 */
	public static void testMicrophone() {

		var microphone = new Microphone();
		var speaker = new Speaker();

		microphone.connectOutputs(speaker);

		visualizeOutputs(microphone);
		createSynthesizer(microphone);
	}

	/**
	 * Tests the {@link Mp3Player} module.
	 *
	 * @since 0.0.9
	 */
	public static void testMp3Player() {

		var player = new Mp3Player(TEST_MP3_PATH);
		var speaker = new Speaker();

		player.connectOutputs(speaker);

		visualizeOutputs(player);
		createSynthesizer(player);
	}

	/**
	 * Test computing frame rate. When there is no speaker to pace the other modules, but we still want modules to
	 * respect a given frame rate, we can set a computing frame rate on one of the modules.
	 *
	 * @since 0.0.9
	 */
	public static void testComputingFrameRate() {

		var player = new Mp3Player(TEST_MP3_PATH);
		var leftSpectrum = new SpectrumAnalyzer("left");
		var rightSpectrum = new SpectrumAnalyzer("right");

		leftSpectrum.setComputingFrameRate(Settings.INSTANCE.frameRate());

		player.connectOutputs(leftSpectrum, rightSpectrum);
		createSynthesizer(player);
	}

	/**
	 * Adds 1 spectrum and 1 oscilloscope for each output of the given module.
	 *
	 * @param modules modules to visualize
	 * @since 0.1.8
	 */
	private static void visualizeOutputs(Module... modules) {

		for (var module : modules) {

			for (var output : module.outputs()) {

				var oscilloscope = new Oscilloscope(Oscilloscope.DEFAULT_NAME + "_" + output.name());
				var spectrum = new SpectrumAnalyzer(SpectrumAnalyzer.DEFAULT_NAME + "_" + output.name());

				output.connect(oscilloscope.input());
				output.connect(spectrum.input());
			}
		}
	}

	/**
	 * Creates a new synthesizer and add given module to it.
	 * Also adds recursively every module connected directly or not to the given module.
	 *
	 * @param module module to add to the created synthesizer
	 * @since 0.2.0
	 */
	private static void createSynthesizer(Module module) {

		var synthesizer = new Synthesizer();
		synthesizer.addChain(module);

		invokeLater(() -> synthesizer.getView().flowLayout(null));
	}
}

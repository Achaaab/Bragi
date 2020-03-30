package com.github.achaaab.bragi;

import com.github.achaaab.bragi.module.ADSR;
import com.github.achaaab.bragi.module.Keyboard;
import com.github.achaaab.bragi.module.LFO;
import com.github.achaaab.bragi.module.LowPassVCF;
import com.github.achaaab.bragi.module.Microphone;
import com.github.achaaab.bragi.module.Mp3FilePlayer;
import com.github.achaaab.bragi.module.Oscilloscope;
import com.github.achaaab.bragi.module.PinkNoiseGenerator;
import com.github.achaaab.bragi.module.Speaker;
import com.github.achaaab.bragi.module.SpectrumAnalyzer;
import com.github.achaaab.bragi.module.Theremin;
import com.github.achaaab.bragi.module.VCA;
import com.github.achaaab.bragi.module.VCO;
import com.github.achaaab.bragi.module.WavFilePlayer;
import com.github.achaaab.bragi.module.WhiteNoiseGenerator;
import org.slf4j.Logger;

import java.nio.file.Path;

import static com.github.achaaab.bragi.ResourceUtils.getPath;
import static com.github.achaaab.bragi.common.wave.Waveform.SAWTOOTH_TRIANGULAR;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * test bank
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.9
 */
public class Test {

	private static final Logger LOGGER = getLogger(Test.class);

	private static final Path TEST_MP3_PATH = getPath("test.mp3");
	private static final Path TEST_WAV_PATH = getPath("test.wav");

	/**
	 * @param arguments none
	 * @since 0.0.9
	 */
	public static void main(String... arguments) {
		testWavFilePlayer();
	}

	/**
	 * Tests tremolo effect.
	 *
	 * @since 0.1.3
	 */
	public static void testTremolo() {

		var adsr = new ADSR();
		var keyboard = new Keyboard();
		var vco = new VCO();
		var vcaEnvelope = new VCA("vca_envelope");
		var speaker = new Speaker();
		var lfo = new LFO();
		var vcaTremolo = new VCA("vca_tremolo");

		// main chain
		keyboard.connectTo(vco);
		vco.connectTo(vcaEnvelope);
		vcaEnvelope.connectTo(vcaTremolo);
		speaker.connectFrom(vcaTremolo, vcaTremolo);

		// ADSR + tremolo
		keyboard.getGate().connect(adsr.getGate());
		adsr.connect(vcaEnvelope.getGain());
		lfo.connect(vcaTremolo.getGain());

		// some tuning
		vco.setWaveform(SAWTOOTH_TRIANGULAR);
		adsr.setAttack(5000.0);
		adsr.setRelease(2.0);

		// visualization
		var oscilloscope = new Oscilloscope();
		var spectrum = new SpectrumAnalyzer();
		vcaTremolo.connectTo(oscilloscope);
		vcaTremolo.connectTo(spectrum);
	}

	/**
	 * Tests the {@link ADSR} module.
	 *
	 * @since 0.1.1
	 */
	public static void testAdsr() {

		var adsr = new ADSR();
		var keyboard = new Keyboard();
		var vco = new VCO();
		var vca = new VCA();
		var speaker = new Speaker();

		keyboard.connectTo(vco);
		vco.connectTo(vca);
		speaker.connectFrom(vca, vca);
		keyboard.getGate().connect(adsr.getGate());
		adsr.connect(vca.getGain());
	}

	/**
	 * Tests the {@link WavFilePlayer} module.
	 *
	 * @since 0.1.0
	 */
	public static void testWavFilePlayer() {

		var player = new WavFilePlayer(TEST_WAV_PATH.toFile());
		var speaker = new Speaker();

		player.getOutputs().get(0).connect(speaker.getInputs().get(0));
		player.getOutputs().get(1).connect(speaker.getInputs().get(1));
	}

	/**
	 * Tests the {@link LowPassVCF} module.
	 *
	 * @since 0.0.9
	 */
	public static void testLowPassVcf() {

		var noise = new WhiteNoiseGenerator();
		var filter = new LowPassVCF();
		var spectrum = new SpectrumAnalyzer();
		var speaker = new Speaker();

		noise.connectTo(filter);
		filter.connectTo(spectrum);
		speaker.connectFrom(filter, filter);
	}

	/**
	 * Tests the {@link PinkNoiseGenerator} module.
	 *
	 * @since 0.0.9
	 */
	public static void testPinkNoiseGenerator() {

		var noise = new PinkNoiseGenerator();
		var spectrum = new SpectrumAnalyzer();
		var speaker = new Speaker();
		var oscilloscope = new Oscilloscope();

		noise.connectTo(spectrum);
		noise.connectTo(oscilloscope);
		speaker.connectFrom(noise, noise);
	}

	/**
	 * Tests the {@link WhiteNoiseGenerator} module.
	 *
	 * @since 0.0.9
	 */
	public static void testWhiteNoiseGenerator() {

		var noise = new WhiteNoiseGenerator();
		var spectrum = new SpectrumAnalyzer();
		var speaker = new Speaker();

		noise.connectTo(spectrum);
		speaker.connectFrom(noise, noise);
	}

	/**
	 * Tests the {@link LFO} module.
	 *
	 * @since 0.0.9
	 */
	public static void testLfo() {

		var vco = new VCO();
		var speaker = new Speaker();
		var spectrum = new SpectrumAnalyzer();
		var lfo = new LFO();

		vco.connectTo(spectrum);
		speaker.connectFrom(vco, vco);
		lfo.connectTo(vco);
	}

	/**
	 * Tests the {@link SpectrumAnalyzer} module.
	 *
	 * @since 0.0.9
	 */
	public static void testSpectrumAnalyzer() {

		var theremin = new Theremin();
		var vco = new VCO();
		var vca = new VCA();
		var spectrum = new SpectrumAnalyzer();

		spectrum.setComputingFrameRate(44100);

		theremin.connectTo(vco);
		vco.connectTo(vca);
		vca.connectTo(spectrum);
		theremin.getVolume().connect(vca.getGain());
	}

	/**
	 * Tests the {@link Keyboard} module.
	 *
	 * @since 0.0.9
	 */
	public static void testKeyboard() {

		var keyboard = new Keyboard();
		var vco = new VCO();
		var speaker = new Speaker();
		var oscilloscope = new Oscilloscope();

		keyboard.connectTo(vco);
		vco.connectTo(oscilloscope);
		speaker.connectFrom(vco, vco);
	}

	/**
	 * Tests the {@link Theremin} module.
	 *
	 * @since 0.0.9
	 */
	public static void testTheremin() {

		var theremin = new Theremin();
		var vco = new VCO();
		var vca = new VCA();
		var oscilloscope = new Oscilloscope();

		theremin.setComputingFrameRate(44100);

		theremin.connectTo(vco);
		vco.connectTo(vca);
		vca.connectTo(oscilloscope);

		theremin.getVolume().connect(vca.getGain());
	}

	/**
	 * Tests the {@link Oscilloscope} module.
	 *
	 * @since 0.0.9
	 */
	public static void testOscilloscope() {

		var vco = new VCO();
		var oscilloscope = new Oscilloscope();

		oscilloscope.setComputingFrameRate(44100);

		vco.connectTo(oscilloscope);
	}

	/**
	 * Tests the {@link Microphone} module.
	 *
	 * @since 0.0.9
	 */
	public static void testMicrophone() {

		var microphone = new Microphone();
		var spectrumLeft = new SpectrumAnalyzer("left");
		var spectrumRight = new SpectrumAnalyzer("right");
		var oscilloscopeLeft = new Oscilloscope("left");
		var oscilloscopeRight = new Oscilloscope("right");
		var speaker = new Speaker();

		microphone.connectTo(spectrumLeft, spectrumRight);
		microphone.connectTo(oscilloscopeLeft, oscilloscopeRight);

		microphone.getOutputs().get(0).connect(speaker.getInputs().get(0));
		microphone.getOutputs().get(1).connect(speaker.getInputs().get(1));
	}

	/**
	 * Tests the {@link Mp3FilePlayer} module.
	 *
	 * @since 0.0.9
	 */
	public static void testMp3FilePlayer() {

		var player = new Mp3FilePlayer(TEST_MP3_PATH);
		var speaker = new Speaker();

		player.getOutputs().get(0).connect(speaker.getInputs().get(0));
		player.getOutputs().get(1).connect(speaker.getInputs().get(1));
	}

	/**
	 * Test computing frame rate. When there is no speaker to pace the other modules but we still want modules to
	 * respect a given frame rate, we can set a computing frame rate on one of the modules.
	 *
	 * @since 0.0.9
	 */
	public static void testComputingFrameRate() {

		var player = new Mp3FilePlayer(TEST_MP3_PATH);

		var leftSpectrum = new SpectrumAnalyzer("left_spectrum");
		var rightSpectrum = new SpectrumAnalyzer("right spectrum");

		leftSpectrum.setComputingFrameRate(11025);

		player.connectTo(leftSpectrum, rightSpectrum);
	}
}
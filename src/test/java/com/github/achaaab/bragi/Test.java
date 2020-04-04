package com.github.achaaab.bragi;

import com.github.achaaab.bragi.common.Settings;
import com.github.achaaab.bragi.module.ADSR;
import com.github.achaaab.bragi.module.DCG;
import com.github.achaaab.bragi.module.HighPassVCF;
import com.github.achaaab.bragi.module.Keyboard;
import com.github.achaaab.bragi.module.LFO;
import com.github.achaaab.bragi.module.LowPassVCF;
import com.github.achaaab.bragi.module.Microphone;
import com.github.achaaab.bragi.module.Mp3Player;
import com.github.achaaab.bragi.module.Oscilloscope;
import com.github.achaaab.bragi.module.PinkNoiseGenerator;
import com.github.achaaab.bragi.module.Speaker;
import com.github.achaaab.bragi.module.SpectrumAnalyzer;
import com.github.achaaab.bragi.module.Theremin;
import com.github.achaaab.bragi.module.VCA;
import com.github.achaaab.bragi.module.VCO;
import com.github.achaaab.bragi.module.WavPlayer;
import com.github.achaaab.bragi.module.WhiteNoiseGenerator;
import org.slf4j.Logger;

import java.nio.file.Path;

import static com.github.achaaab.bragi.ResourceUtils.getPath;
import static com.github.achaaab.bragi.common.wave.Waveform.SAWTOOTH_TRIANGULAR;
import static com.github.achaaab.bragi.common.wave.Waveform.SINE;
import static com.github.achaaab.bragi.common.wave.Waveform.TRIANGLE;
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

	/**
	 * @param arguments none
	 * @since 0.0.9
	 */
	public static void main(String... arguments) {
		testMp3Player();
	}

	/**
	 * Tests a band-pass filter with one low-pass VCF and one high-pass VCF.
	 *
	 * @since 0.1.6
	 */
	public static void testBandPassFilter() {

		var noise = new WhiteNoiseGenerator();
		var low = new LowPassVCF();
		var high = new HighPassVCF();
		var speaker = new Speaker();
		var spectrum = new SpectrumAnalyzer();

		noise.connect(low);
		low.connect(high);
		speaker.connectInputs(high, high);
		high.connect(spectrum);
	}

	/**
	 * Tests a piano-like sound (currently far from it).
	 *
	 * @since 0.1.6
	 */
	public static void testPiano() {

		// main chain
		var keyboard = new Keyboard();
		var vco = new VCO();
		var filter = new LowPassVCF();
		var envelope = new VCA("envelope");
		var tremolo = new VCA("tremolo");
		var speaker = new Speaker();
		keyboard.connect(vco);
		vco.connect(filter);
		filter.connect(envelope);
		envelope.connect(tremolo);
		speaker.connectInputs(tremolo, tremolo);

		// envelope
		var adsr = new ADSR();
		keyboard.getGate().connect(adsr.getGate());
		adsr.connect(envelope.getGain());

		// a little bit of tremolo
		var lfo = new LFO();
		lfo.connect(tremolo.getGain());

		lfo.setWaveform(SINE);
		vco.setWaveform(TRIANGLE);
		lfo.setFrequency(6.875);
		lfo.setLowerPeak(-0.1f);
		lfo.setUpperPeak(0.0f);
		adsr.setAttack(1000);
		adsr.setRelease(100);
		adsr.setSustain(-0.2f);
		adsr.setRelease(3.0f);
		filter.setCutoffFrequency(7040.f);

		var spectrum = new SpectrumAnalyzer();
		var oscilloscope = new Oscilloscope();
		tremolo.connect(spectrum, oscilloscope);
	}

	/**
	 * Tests filter "self" oscillation.
	 * <p>
	 * Due to the architecture of Bragi, the filter needs an input to produce an output.
	 * We could use a DCG with 0 volt but it would not trigger the oscillation.
	 * Like in an analog synthesizer, we need a tiny noise to trigger the oscillation.
	 *
	 * @since 0.1.6
	 */
	public static void testSelfOscillatingFilter() {

		var noise = new WhiteNoiseGenerator();
		var vca = new VCA();
		var filter = new LowPassVCF();
		var speaker = new Speaker();

		// very low noise (-200 dB) is enough to trigger "self" oscillation of filter
		vca.setInitialGain(-200);

		// maximum emphasis
		filter.setEmphasis(1.0f);

		noise.connect(vca);
		vca.connect(filter);
		speaker.connectInputs(filter, filter);

		// visualization
		var oscilloscope = new Oscilloscope();
		var spectrum = new SpectrumAnalyzer();
		filter.connect(oscilloscope, spectrum);
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
		keyboard.connect(vco);
		vco.connect(vcaEnvelope);
		vcaEnvelope.connect(vcaTremolo);
		speaker.connectInputs(vcaTremolo, vcaTremolo);

		// ADSR + tremolo
		keyboard.getGate().connect(adsr.getGate());
		adsr.connect(vcaEnvelope.getGain());
		lfo.connect(vcaTremolo.getGain());

		// some tuning
		vco.setWaveform(SAWTOOTH_TRIANGULAR);
		adsr.setAttack(1000.0);
		adsr.setRelease(2.0);

		// visualization
		var oscilloscope = new Oscilloscope();
		var spectrum = new SpectrumAnalyzer();
		vcaTremolo.connect(oscilloscope, spectrum);
	}

	/**
	 * Tests the {@link DCG} module.
	 *
	 * @since 0.1.6
	 */
	public static void testDcg() {

		var dcg = new DCG();
		var oscilloscope = new Oscilloscope();

		dcg.setComputingFrameRate(Settings.INSTANCE.frameRate());

		dcg.connect(oscilloscope);
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

		keyboard.connect(vco);
		vco.connect(vca);
		speaker.connectInputs(vca, vca);
		keyboard.getGate().connect(adsr.getGate());
		adsr.connect(vca.getGain());
	}

	/**
	 * Tests the {@link WavPlayer} module.
	 * <p>
	 * TODO Add a module to change the sample rate.
	 *
	 * @since 0.1.0
	 */
	public static void testWavPlayer() {

		var player = new WavPlayer(TEST_WAV_PATH);
		var speaker = new Speaker();

		player.connectOutputs(speaker);
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

		noise.connect(filter);
		filter.connect(spectrum);
		speaker.connectInputs(filter, filter);

		var oscilloscope = new Oscilloscope();
		filter.connect(oscilloscope);
	}

	/**
	 * Tests the {@link HighPassVCF} module.
	 *
	 * @since 0.1.6
	 */
	public static void testHighPassVcf() {

		var noise = new WhiteNoiseGenerator();
		var filter = new HighPassVCF();
		var spectrum = new SpectrumAnalyzer();
		var speaker = new Speaker();

		noise.connect(filter);
		filter.connect(spectrum);
		speaker.connectInputs(filter, filter);

		var oscilloscope = new Oscilloscope();
		filter.connect(oscilloscope);
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

		noise.connect(spectrum, oscilloscope);
		speaker.connectInputs(noise, noise);
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

		noise.connect(spectrum);
		speaker.connectInputs(noise, noise);
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

		vco.connect(spectrum);
		speaker.connectInputs(vco, vco);
		lfo.connect(vco);
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

		spectrum.setComputingFrameRate(Settings.INSTANCE.frameRate());

		theremin.connect(vco);
		vco.connect(vca);
		vca.connect(spectrum);
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

		keyboard.connect(vco);
		vco.connect(oscilloscope);
		speaker.connectInputs(vco, vco);
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

		theremin.setComputingFrameRate(Settings.INSTANCE.frameRate());

		theremin.connect(vco);
		vco.connect(vca);
		vca.connect(oscilloscope);

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

		oscilloscope.setComputingFrameRate(Settings.INSTANCE.frameRate());

		vco.connect(oscilloscope);
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

		microphone.connectOutputs(spectrumLeft, spectrumRight);
		microphone.connectOutputs(oscilloscopeLeft, oscilloscopeRight);
		microphone.connectOutputs(speaker);
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
	}

	/**
	 * Test computing frame rate. When there is no speaker to pace the other modules but we still want modules to
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
	}
}
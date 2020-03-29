package fr.guehenneux.bragi;

import fr.guehenneux.bragi.common.MalformedWavFileException;
import fr.guehenneux.bragi.module.Keyboard;
import fr.guehenneux.bragi.module.LFO;
import fr.guehenneux.bragi.module.LowPassVCF;
import fr.guehenneux.bragi.module.Microphone;
import fr.guehenneux.bragi.module.Mp3FilePlayer;
import fr.guehenneux.bragi.module.Oscilloscope;
import fr.guehenneux.bragi.module.PinkNoiseGenerator;
import fr.guehenneux.bragi.module.Speaker;
import fr.guehenneux.bragi.module.SpectrumAnalyzer;
import fr.guehenneux.bragi.module.Theremin;
import fr.guehenneux.bragi.module.VCA;
import fr.guehenneux.bragi.module.VCO;
import fr.guehenneux.bragi.module.WavFilePlayer;
import fr.guehenneux.bragi.module.WhiteNoiseGenerator;
import javazoom.jl.decoder.JavaLayerException;
import org.slf4j.Logger;

import javax.sound.sampled.LineUnavailableException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Jonathan Guéhenneux
 */
public class Test {

	private static final Logger LOGGER = getLogger(Test.class);

	private static final Path TEST_MP3_PATH = Paths.get(
			"/media/jonathan/media/Musique/Aaliyah/Aaliyah (2001)/15. Try Again.mp3");

	/**
	 * @param arguments
	 * @throws LineUnavailableException
	 * @throws MalformedWavFileException
	 * @throws IOException
	 * @throws JavaLayerException
	 */
	public static void main(String... arguments) throws LineUnavailableException, IOException, MalformedWavFileException,
			JavaLayerException {

		testLowPassVcf();
	}

	/**
	 * Tests the {@link LowPassVCF} module, audibly with a {@link Speaker} module
	 * and visually with a {@link SpectrumAnalyzer} module.
	 *
	 * @throws LineUnavailableException if no line was found for the {@link Speaker} module
	 */
	public static void testLowPassVcf() throws LineUnavailableException {

		var whiteNoise = new WhiteNoiseGenerator("noise");
		var filter = new LowPassVCF("filter");
		var spectrum = new SpectrumAnalyzer("spectrum");
		var speaker = new Speaker("speaker");

		whiteNoise.connectTo(filter);
		filter.connectTo(spectrum);
		speaker.connectFrom(filter, filter);
	}

	/**
	 * @throws LineUnavailableException
	 */
	public static void testPinkNoiseGenerator() throws LineUnavailableException {

		var pinkNoise = new PinkNoiseGenerator("pink_noise");
		var spectrum = new SpectrumAnalyzer("spectrum");
		var speaker = new Speaker("speaker");
		var oscilloscope = new Oscilloscope("oscilloscope");

		pinkNoise.connectTo(spectrum);
		pinkNoise.connectTo(oscilloscope);
		speaker.connectFrom(pinkNoise, pinkNoise);
	}

	/**
	 * Tests the white noise generator.
	 *
	 * @throws LineUnavailableException
	 */
	public static void testWhiteNoiseGenerator() throws LineUnavailableException {

		var whiteNoise = new WhiteNoiseGenerator("white_noise");
		var spectrum = new SpectrumAnalyzer("spectrum");
		var speaker = new Speaker("speaker");

		whiteNoise.connectTo(spectrum);
		speaker.connectFrom(whiteNoise, whiteNoise);
	}

	/**
	 * Test the LFO module.
	 *
	 * @throws LineUnavailableException
	 */
	public static void testLfo() throws LineUnavailableException {

		var vco = new VCO("vco");
		var speaker = new Speaker("speaker");
		var spectrum = new SpectrumAnalyzer("spectrum");
		var lfo = new LFO("lfo");

		vco.connectTo(spectrum);
		speaker.connectFrom(vco, vco);
		lfo.connectTo(vco);
	}

	/**
	 * Tests the spectrum analyzer.
	 */
	public static void testSpectrumAnalyzer() throws LineUnavailableException {

		var theremin = new Theremin("theremin");
		var vco = new VCO("vco");
		var vca = new VCA("vca");
		var spectrum = new SpectrumAnalyzer("spectrum");
		spectrum.setComputingFrameRate(44100);

		theremin.connectTo(vco);
		vco.connectTo(vca);
		vca.connectTo(spectrum);
		theremin.getVolume().connect(vca.getGain());
	}

	/**
	 * @throws LineUnavailableException
	 */
	public static void testKeyboard() throws LineUnavailableException {

		var keyboard = new Keyboard("keyboard");
		var vco = new VCO("vco");
		var speaker = new Speaker("speaker");
		var oscilloscope = new Oscilloscope("oscilloscope");

		keyboard.connectTo(vco);
		vco.connectTo(oscilloscope);
		speaker.connectFrom(vco, vco);
	}

	/**
	 * Test of theremin with VCO.
	 */
	public static void testThereminVcoVca() throws LineUnavailableException {

		var theremin = new Theremin("theremin");
		var vco = new VCO("vco");
		var volumeFilter = new LowPassVCF("volume_filter");
		var vca = new VCA("vca");
		var filter = new LowPassVCF("filter");
		var speaker = new Speaker("speaker");
		var oscilloscope = new Oscilloscope("oscilloscope");
		var oscilloscopeVco = new Oscilloscope("oscilloscope_vco");
		var lfo = new LFO("lfo");

		theremin.connect(vco.getModulation());
		vco.connectTo(vca);
		vca.connectTo(filter);
		speaker.connectFrom(filter, filter);
		lfo.connect(filter.getModulation());
		filter.connectTo(oscilloscope);
		theremin.getVolume().connect(vca.getGain());
		//volumeFilter.connect(vca.getGain());
		vca.connectTo(oscilloscopeVco);
	}

	/**
	 * Basic test of theremin.
	 */
	public static void testThereminBasic() {

		var theremin = new Theremin("theremin");
		var vco = new VCO("vco");
		var vca = new VCA("vca");

		var pitch = new Oscilloscope("pitch");
		var volume = new Oscilloscope("volume");
		var wave = new Oscilloscope("wave");

		theremin.connectTo(vco);
		vco.connectTo(vca);
		vca.connectTo(wave);
		theremin.getVolume().connect(vca.getGain());

		theremin.setComputingFrameRate(44100);

		theremin.getPitch().connect(pitch.getInput());
		theremin.getVolume().connect(volume.getInput());
	}

	/**
	 * @throws IOException
	 * @throws LineUnavailableException
	 */
	public static void testOscilloscope() throws IOException, LineUnavailableException {

		var vco = new VCO("vco");
		var oscilloscope = new Oscilloscope("oscilloscope");

		oscilloscope.setComputingFrameRate(44100);

		vco.connectTo(oscilloscope);
	}

	/**
	 * Test computing frame rate. When there is no speaker to pace the other modules but we still want modules to
	 * respect a given frame rate, we can set a computing frame rate on one of the output modules.
	 *
	 * @throws IOException
	 */
	public static void testComputingFrameRate() throws IOException {

		var player = new Mp3FilePlayer("player", TEST_MP3_PATH);
		var leftSpectrum = new SpectrumAnalyzer("left_spectrum");
		var rightSpectrum = new SpectrumAnalyzer("right spectrum");

		leftSpectrum.setComputingFrameRate(4410);

		player.connectTo(leftSpectrum, rightSpectrum);
	}

	/**
	 * @throws LineUnavailableException
	 */
	public static void testMicro() throws LineUnavailableException {

		var microphone = new Microphone("microphone");
		var spectrumLeft = new SpectrumAnalyzer("spectrum_left");
		var spectrumRight = new SpectrumAnalyzer("spectrum_right");
		var oscilloscopeLeft = new Oscilloscope("oscilloscope_left");
		var oscilloscopeRight = new Oscilloscope("oscilloscope_right");
		var speaker = new Speaker("speaker");

		microphone.connectTo(spectrumLeft, spectrumRight);
		microphone.connectTo(oscilloscopeLeft, oscilloscopeRight);
		microphone.getOutputs().get(0).connect(speaker.getInputs().get(0));
		microphone.getOutputs().get(1).connect(speaker.getInputs().get(1));
	}

	/**
	 * @throws IOException
	 * @throws LineUnavailableException
	 */
	public static void testMP3() throws IOException, LineUnavailableException {

		var speaker = new Speaker("speaker");
		var player = new Mp3FilePlayer("mp3_player", TEST_MP3_PATH);
		var oscilloscopeLeft = new Oscilloscope("left_oscilloscope");
		var oscilloscopeRight = new Oscilloscope("right_oscilloscope");
		var spectrumAnalyzerLeft = new SpectrumAnalyzer("left_spectrum_analyzer");
		var spectrumAnalyzerRight = new SpectrumAnalyzer("right_spectrum_analyzer");

		var outputs = player.getOutputs();

		outputs.get(0).connect(speaker.getInputs().get(0));
		outputs.get(1).connect(speaker.getInputs().get(1));
		outputs.get(0).connect(spectrumAnalyzerLeft.getInput());
		outputs.get(1).connect(spectrumAnalyzerRight.getInput());
		outputs.get(0).connect(oscilloscopeLeft.getInput());
		outputs.get(1).connect(oscilloscopeRight.getInput());
	}

	/**
	 * @throws IOException
	 * @throws LineUnavailableException
	 * @throws MalformedWavFileException
	 */
	public static void testWav() throws IOException, LineUnavailableException, MalformedWavFileException {

		var speaker = new Speaker("speaker");
		var waveFilePlayer = new WavFilePlayer("wav_player", new File("sample.wav"));
		var oscilloscopeLeft = new Oscilloscope("left_oscilloscope");
		var oscilloscopeRight = new Oscilloscope("right_oscilloscope");
		var spectrumAnalyzerLeft = new SpectrumAnalyzer("left_spectrum_analyzer");
		var spectrumAnalyzerRight = new SpectrumAnalyzer("right_spectrum_analyzer");

		waveFilePlayer.getOutputs().get(0).connect(speaker.getInputs().get(0));
		waveFilePlayer.getOutputs().get(1).connect(speaker.getInputs().get(1));
		waveFilePlayer.getOutputs().get(0).connect(oscilloscopeLeft.getInput());
		waveFilePlayer.getOutputs().get(1).connect(oscilloscopeRight.getInput());
		waveFilePlayer.getOutputs().get(0).connect(spectrumAnalyzerLeft.getInput());
		waveFilePlayer.getOutputs().get(1).connect(spectrumAnalyzerRight.getInput());
	}
}
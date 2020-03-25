package fr.guehenneux.bragi;

import fr.guehenneux.bragi.module.model.ADSR;
import fr.guehenneux.bragi.module.model.HighPassVCF;
import fr.guehenneux.bragi.module.model.Key;
import fr.guehenneux.bragi.module.model.Keyboard;
import fr.guehenneux.bragi.module.model.LFO;
import fr.guehenneux.bragi.module.model.LowPassVCF;
import fr.guehenneux.bragi.module.model.Microphone;
import fr.guehenneux.bragi.module.model.Mp3FilePlayer;
import fr.guehenneux.bragi.module.model.Oscilloscope;
import fr.guehenneux.bragi.module.model.Sampler;
import fr.guehenneux.bragi.module.model.Speaker;
import fr.guehenneux.bragi.module.model.SpectrumAnalyzer;
import fr.guehenneux.bragi.module.model.Theremin;
import fr.guehenneux.bragi.module.model.VCA;
import fr.guehenneux.bragi.module.model.VCO;
import fr.guehenneux.bragi.module.model.WavFilePlayer;
import fr.guehenneux.bragi.module.model.WhiteNoiseGenerator;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.jlp;
import org.slf4j.Logger;

import javax.sound.sampled.LineUnavailableException;
import java.io.File;
import java.io.FileNotFoundException;
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

		testLowPassFiler();
	}

	/**
	 * @throws LineUnavailableException
	 */
	public static void testLowPassFiler() throws LineUnavailableException {

		var filter = new LowPassVCF("filter");
		var keyboard = new Keyboard("keyboard");
		var speaker = new Speaker("speaker");
		var spectrum = new SpectrumAnalyzer("oscilloscope");

		keyboard.connectTo(filter);
		speaker.connectFrom(filter, filter);
		filter.connectTo(spectrum);
	}

	/**
	 * @throws LineUnavailableException
	 */
	public static void testKeyboard() throws LineUnavailableException {

		var keyboard = new Keyboard("keyboard");
		var speaker = new Speaker("speaker");

		speaker.connectFrom(keyboard, keyboard);
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
		var lfo = new LFO("lfo", 5);

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
		var pitch = new Oscilloscope("pitch");
		var volume = new Oscilloscope("volume");

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

		new Thread(() -> vco.connectTo(oscilloscope)).start();
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
	public static void montage9() throws LineUnavailableException {

		var whiteNoiseGenerator = new WhiteNoiseGenerator("white_noise_generator");
		var speaker = new Speaker("speaker");

		whiteNoiseGenerator.connect(speaker.getInputs());
	}

	/**
	 * @throws LineUnavailableException
	 */
	public static void montage8() throws LineUnavailableException {

		var lfo = new LFO("lfo", 1);
		var keyboard = new Keyboard("keyboard");
		var adsrVca = new ADSR("adsr_vca");
		var adsrFilter = new ADSR("adsr_filter");
		var vca = new VCA("vca");
		var speaker = new Speaker("speaker");
		var oscilloscope = new Oscilloscope("oscilloscope");
		var filter = new LowPassVCF("filter");
		var oscilloscopeAdsrVca = new Oscilloscope("oscilloscope_adsr_vca");
		var oscilloscopeAdsrFilter = new Oscilloscope("oscilloscope_adsr_filter");

		new Thread(() -> lfo.connectTo(keyboard)).start();
		new Thread(() -> keyboard.getOutput().connect(filter.getInput())).start();
		new Thread(() -> keyboard.getGate().connect(adsrVca.getGate())).start();
		new Thread(() -> keyboard.getGate().connect(adsrFilter.getGate())).start();
		new Thread(() -> adsrVca.connect(vca.getGain())).start();
		new Thread(() -> adsrVca.connectTo(oscilloscopeAdsrVca)).start();
		new Thread(() -> adsrFilter.connect(filter.getModulation())).start();
		new Thread(() -> adsrFilter.connectTo(oscilloscopeAdsrFilter)).start();
		new Thread(() -> filter.connect(vca.getInput())).start();
		new Thread(() -> vca.connectTo(oscilloscope)).start();
		new Thread(() -> vca.connect(speaker.getInputs())).start();
	}

	/**
	 * Modules:
	 * <ul>
	 *   <li>VCO</li>
	 *   <li>spectrum analyzer</li>
	 *   <li>stereo speaker</li>
	 * </ul>
	 * <p>
	 * Connections:
	 * <ul>
	 *   <li>VCO -> spectrum analyzer</li>
	 *   <li>VCO -> left speaker</li>
	 *   <li>VCO -> right speaker</li>
	 * </ul>
	 */
	public static void montage3() throws LineUnavailableException {

		var vco = new VCO("VCO");
		var speaker = new Speaker("speaker");
		var spectrumAnalyzer = new SpectrumAnalyzer("spectrumAnalyzer");
		var oscilloscope = new Oscilloscope("oscilloscope");

		vco.connectTo(oscilloscope);
		vco.connectTo(spectrumAnalyzer);
		vco.connect(speaker.getInputs());
	}

	public static void montage4() throws LineUnavailableException {

		var microphone = new Microphone("microphone");
		var leftSampler = new Sampler("left_sampler");
		var rightSampler = new Sampler("right_sampler");
		var speaker = new Speaker("speaker");

		var leftFilter = new LowPassVCF("left_filter");
		var rightFilter = new LowPassVCF("right_filter");

		new Thread(() -> microphone.connectTo(leftFilter, rightFilter)).start();
		new Thread(() -> speaker.connectFrom(leftFilter, rightFilter)).start();

		/*
		new Thread(() -> microphone.connectTo(leftSampler, rightSampler)).start();
		new Thread(() -> leftSampler.connectTo(leftFilter)).start();
		new Thread(() -> rightSampler.connectTo(rightFilter)).start();
		new Thread(() -> speaker.connectFrom(leftFilter, rightFilter)).start();
		*/
	}

	public static void montage5() throws IOException, LineUnavailableException {

		var player = new Mp3FilePlayer("mp3_player", TEST_MP3_PATH);
		var leftSampler = new Sampler("left_sampler");
		var rightSampler = new Sampler("right_sampler");
		var speaker = new Speaker("speaker");

		player.connectTo(leftSampler, rightSampler);
		speaker.connectFrom(leftSampler, rightSampler);
	}

	/**
	 * @throws LineUnavailableException
	 */
	public static void montage6() throws  LineUnavailableException {

		var vco = new VCO("vco");
		var oscilloscope = new Oscilloscope("oscilloscope");
		var speaker = new Speaker("speaker");

		vco.connectTo(oscilloscope);
		vco.connect(speaker.getInputs());
	}

	/**
	 * @throws LineUnavailableException
	 */
	public static void montage7() throws  LineUnavailableException {

		var keyboard = new Keyboard("keyboard");
		var speaker = new Speaker("speaker");
		var adsr = new ADSR("adsr");
		var vca = new VCA("vca");
		var oscilloscope = new Oscilloscope("oscilloscope");
		var spectrum = new SpectrumAnalyzer("spectrum");

		new Thread(() -> keyboard.connectTo(vca)).start();
		new Thread(() -> vca.connect(speaker.getInputs())).start();
		new Thread(() -> keyboard.getGate().connect(adsr.getGate())).start();
		new Thread(() -> adsr.connect(vca.getGain())).start();
		new Thread(() -> vca.connectTo(oscilloscope)).start();
		new Thread(() -> vca.connectTo(spectrum)).start();
	}

	/**
	 * @throws FileNotFoundException
	 * @throws LineUnavailableException
	 */
	public static void testLowPassFilter() throws IOException, LineUnavailableException {

		var speaker = new Speaker("speaker");
		var player = new Mp3FilePlayer("player", TEST_MP3_PATH);
		var leftFilter = new LowPassVCF("left_filter");
		var rightFilter = new LowPassVCF("rightFilter");

		new Thread(() -> player.connectTo(leftFilter, rightFilter)).start();
		new Thread(() -> speaker.connectFrom(leftFilter, rightFilter)).start();
	}

	/**
	 * @throws LineUnavailableException
	 * @throws FileNotFoundException
	 */
	public static void testFiltrePasseBande() throws LineUnavailableException, IOException {

		var speaker = new Speaker("Hauts-parleurs");
		var player = new Mp3FilePlayer("Lecteur de fichier MP3", TEST_MP3_PATH);
		var spectrumAnalyzerLeft = new SpectrumAnalyzer("Analyseur de spectre, gauche");
		var spectrumAnalyzerRight = new SpectrumAnalyzer("Analyseur de spectre, droit");
		var oscilloscopeLeft = new Oscilloscope("Oscilloscope, gauche");
		var oscilloscopeRight = new Oscilloscope("Oscilloscope, droit");
		var highFilterLeft = new HighPassVCF("Filtre passe-haut, gauche");
		var highFilterRight = new HighPassVCF("Filtre passe-haut, droit");
		var lowFilterLeft = new LowPassVCF("Filtre passe-bas, gauche");
		var lowFilterRight = new LowPassVCF("Filtre passe-bas, droit");
		var spectrumAnalyzerFilterLeft = new SpectrumAnalyzer("Analyseur de spectre filtré, gauche");
		var spectrumAnalyzerFilterRight = new SpectrumAnalyzer("Analyseur de spectre filtré, droit");

		lowFilterLeft.setCutOffFrequency(220);
		lowFilterRight.setCutOffFrequency(220);
		highFilterLeft.setCutOffFrequency(880);
		highFilterRight.setCutOffFrequency(880);

		new Thread(() -> player.connectTo(highFilterLeft, highFilterRight)).start();
		new Thread(() -> player.connectTo(spectrumAnalyzerLeft, spectrumAnalyzerRight)).start();
		new Thread(() -> player.connectTo(oscilloscopeLeft, oscilloscopeRight)).start();
		new Thread(() -> highFilterLeft.connectTo(lowFilterLeft)).start();
		new Thread(() -> highFilterRight.connectTo(lowFilterRight)).start();
		new Thread(() -> speaker.connectFrom(lowFilterLeft, lowFilterRight)).start();
		new Thread(() -> lowFilterLeft.connectTo(spectrumAnalyzerFilterLeft)).start();
		new Thread(() -> lowFilterRight.connectTo(spectrumAnalyzerFilterRight)).start();
	}

	public static void testFiltrePasseHaut() throws LineUnavailableException, IOException {

		var speaker = new Speaker("Hauts-parleurs");
		var mp3FilePlayer = new Mp3FilePlayer("Lecteur de fichier MP3", TEST_MP3_PATH);
		var spectrumAnalyzerLeft = new SpectrumAnalyzer("Analyseur de spectre, gauche");
		var spectrumAnalyzerRight = new SpectrumAnalyzer("Analyseur de spectre, droit");
		var oscilloscopeLeft = new Oscilloscope("Oscilloscope, gauche");
		var oscilloscopeRight = new Oscilloscope("Oscilloscope, droit");
		var filterLeft = new HighPassVCF("Filtre passe-haut, gauche");
		var filterRight = new HighPassVCF("Filtre passe-haut, droit");
		var spectrumAnalyzerFilterLeft = new SpectrumAnalyzer("Analyseur de spectre filtré, gauche");
		var spectrumAnalyzerFilterRight = new SpectrumAnalyzer("Analyseur de spectre filtré, droit");

		filterLeft.setCutOffFrequency(5000);
		filterRight.setCutOffFrequency(5000);

		var outputs = mp3FilePlayer.getOutputs();

		outputs.get(0).connect(filterLeft.getInput());
		outputs.get(1).connect(filterRight.getInput());
		filterLeft.getOutput().connect(speaker.getInputs().get(0));
		filterRight.getOutput().connect(speaker.getInputs().get(1));
		filterLeft.getOutput().connect(spectrumAnalyzerFilterLeft.getInput());
		filterRight.getOutput().connect(spectrumAnalyzerFilterRight.getInput());

		outputs.get(0).connect(spectrumAnalyzerLeft.getInput());
		outputs.get(1).connect(spectrumAnalyzerRight.getInput());
		outputs.get(0).connect(oscilloscopeLeft.getInput());
		outputs.get(1).connect(oscilloscopeRight.getInput());
	}

	/**
	 * @throws JavaLayerException
	 */
	public static void lireMP3() throws JavaLayerException {
		jlp.createInstance(new String[]{TEST_MP3_PATH.toString()}).play();
	}

	/**
	 * @throws LineUnavailableException
	 */
	public static void testMicro() throws LineUnavailableException {

		var microphone = new Microphone("microphone");
		var spectrum = new SpectrumAnalyzer("spectrum");
		var oscilloscope = new Oscilloscope("oscilloscope");
		var speaker = new Speaker("speaker");

		new Thread(() -> microphone.connectTo(spectrum)).start();
		new Thread(() -> microphone.connectTo(oscilloscope)).start();
		new Thread(() -> microphone.connectTo(speaker)).start();
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

	/**
	 * @throws LineUnavailableException
	 * @throws IOException
	 * @throws MalformedWavFileException
	 */
	public static void montage2() throws LineUnavailableException, IOException, MalformedWavFileException {

		var speaker = new Speaker("Hauts-parleurs");
		var wavFilePlayer = new WavFilePlayer("Lecteur de fichier WAV", new File("sample.wav"));
		var spectrumAnalyzerLeft = new SpectrumAnalyzer("Analyseur de spectre, gauche");
		var spectrumAnalyzerRight = new SpectrumAnalyzer("Analyseur de spectre, droit");
		var oscilloscopeLeft = new Oscilloscope("Oscilloscope, gauche");
		var oscilloscopeRight = new Oscilloscope("Oscilloscope, droit");

		wavFilePlayer.connectTo(speaker);

		var outputs = wavFilePlayer.getOutputs();
		outputs.get(0).connect(spectrumAnalyzerLeft.getInput());
		outputs.get(1).connect(spectrumAnalyzerRight.getInput());
		outputs.get(0).connect(oscilloscopeLeft.getInput());
		outputs.get(1).connect(oscilloscopeRight.getInput());
	}

	/**
	 * @throws LineUnavailableException
	 */
	public static void montage1() throws LineUnavailableException {

		var speaker = new Speaker("speaker");
		var lfo = new LFO("lfo", 1);
		var vco = new VCO("vco");
		var lfoFilter = new LFO("lfo_vcf", 1);
		var highPassFilter = new HighPassVCF("highpass_vcf");
		var lowPassFilter = new LowPassVCF("lowpass_vcf");
		var spectrumAnalyzer = new SpectrumAnalyzer("spectrum_analyzer");
		var oscilloscope = new Oscilloscope("oscilloscope");
		var oscilloscopeVco = new Oscilloscope("oscilloscope_vco");

		lfo.connectTo(vco);
		vco.connectTo(lowPassFilter);
		vco.connectTo(oscilloscopeVco);
		lowPassFilter.connectTo(highPassFilter);
		speaker.connectFrom(highPassFilter, highPassFilter);
		highPassFilter.connectTo(spectrumAnalyzer);
		highPassFilter.connectTo(oscilloscope);
		lfoFilter.connect(lowPassFilter.getModulation());
		lfoFilter.connect(highPassFilter.getModulation());
	}
}
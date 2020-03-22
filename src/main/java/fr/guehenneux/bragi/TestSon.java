package fr.guehenneux.bragi;

import fr.guehenneux.bragi.module.model.ADSR;
import fr.guehenneux.bragi.module.model.HighPassVCF;
import fr.guehenneux.bragi.module.model.Keyboard;
import fr.guehenneux.bragi.module.model.LFO;
import fr.guehenneux.bragi.module.model.LowPassVCF;
import fr.guehenneux.bragi.module.model.Microphone;
import fr.guehenneux.bragi.module.model.Mp3FilePlayer;
import fr.guehenneux.bragi.module.model.Oscilloscope;
import fr.guehenneux.bragi.module.model.Sampler;
import fr.guehenneux.bragi.module.model.Speaker;
import fr.guehenneux.bragi.module.model.SpectrumAnalyzer;
import fr.guehenneux.bragi.module.model.VCA;
import fr.guehenneux.bragi.module.model.VCO;
import fr.guehenneux.bragi.module.model.WavFilePlayer;
import fr.guehenneux.bragi.module.model.WhiteNoiseGenerator;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.jlp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class TestSon {

	private static final Logger LOGGER = getLogger(TestSon.class);

	private static final Path TEST_MP3_PATH = Paths.get(
			"/media/jonathan/media/Musique/Aaliyah/Aaliyah (2001)/15. Try Again.mp3");

	/**
	 * @param arguments
	 * @throws LineUnavailableException
	 * @throws CorruptWavFileException
	 * @throws IOException
	 * @throws JavaLayerException
	 */
	public static void main(String... arguments) throws LineUnavailableException, IOException, CorruptWavFileException,
			JavaLayerException {

		montage8();
	}

	/**
	 * @throws LineUnavailableException
	 */
	public static void montage9() throws LineUnavailableException {

		var whiteNoiseGenerator = new WhiteNoiseGenerator("white_noise_generator");
		var speaker = new Speaker("speaker");

		whiteNoiseGenerator.getOutput().connect(speaker);
	}

	/**
	 * @throws LineUnavailableException
	 */
	public static void montage8() throws LineUnavailableException {

		var lfoKeyboard = new LFO("lfo_keyboard", 1);
		var keyboard = new Keyboard("keyboard");
		var adsrVca = new ADSR("adsr_piano");
		var adsrFilter = new ADSR("adsr_filter");
		var vca = new VCA("vca");
		var speaker = new Speaker("speaker");
		var oscilloscope = new Oscilloscope("oscilloscope");
		var filter = new LowPassVCF("filter");
		var oscilloscopeAdsrVca = new Oscilloscope("oscilloscope_adsr_vca");
		var oscilloscopeAdsrFilter = new Oscilloscope("oscilloscope_adsr_filter");

		lfoKeyboard.getOutput().connect(keyboard.getModulation());
		keyboard.getOutput().connect(filter.getInput());
		keyboard.getGate().connect(adsrVca.getGate());
		keyboard.getGate().connect(adsrFilter.getGate());
		adsrVca.getOutput().connect(vca.getGain());
		adsrVca.getOutput().connect(oscilloscopeAdsrVca);
		adsrFilter.getOutput().connect(filter.getModulation());
		adsrFilter.getOutput().connect(oscilloscopeAdsrFilter);
		filter.getOutput().connect(vca.getInput());
		vca.connect(oscilloscope);
		vca.getOutput().connect(speaker);
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

		var vco = new VCO("VCO", 1);
		var speaker = new Speaker("speaker");
		var spectrumAnalyzer = new SpectrumAnalyzer("spectrumAnalyzer");
		var oscilloscope = new Oscilloscope("oscilloscope");

		vco.connect(oscilloscope);
		vco.connect(spectrumAnalyzer);
		vco.getOutput().connect(speaker);
	}

	public static void montage4() throws LineUnavailableException {

		var microphone = new Microphone("microphone");
		var leftSampler = new Sampler("left_sampler");
		var rightSampler = new Sampler("right_sampler");
		var speaker = new Speaker("speaker");

		microphone.getOutputs().get(0).connect(leftSampler);
		microphone.getOutputs().get(1).connect(rightSampler);
		leftSampler.getOutput().connect(speaker.getInputs().get(0));
		rightSampler.getOutput().connect(speaker.getInputs().get(1));
	}

	public static void montage5() throws IOException, LineUnavailableException {

		var player = new Mp3FilePlayer("mp3_player", TEST_MP3_PATH);
		var leftSampler = new Sampler("left_sampler");
		var rightSampler = new Sampler("right_sampler");
		var speaker = new Speaker("speaker");

		player.getOutputs().get(0).connect(leftSampler);
		player.getOutputs().get(1).connect(rightSampler);
		leftSampler.getOutput().connect(speaker.getInputs().get(0));
		rightSampler.getOutput().connect(speaker.getInputs().get(1));
	}

	/**
	 * @throws LineUnavailableException
	 */
	public static void montage6() throws  LineUnavailableException {

		var vco = new VCO("vco", 440);
		var oscilloscope = new Oscilloscope("oscilloscope");
		var speaker = new Speaker("speaker");

		vco.connect(oscilloscope);
		vco.getOutput().connect(speaker);
	}

	/**
	 * @throws LineUnavailableException
	 */
	public static void montage7() throws  LineUnavailableException {

		var keyboard = new Keyboard("keyboard");
		var speaker = new Speaker("speaker");
		var adsr = new ADSR("adsr");
		var oscilloscope = new Oscilloscope("oscilloscope");
		var vca = new VCA("vca");

		keyboard.getOutput().connect(vca.getInput());
		keyboard.getGate().connect(adsr.getGate());
		adsr.getOutput().connect(vca.getGain());
		adsr.getOutput().connect(oscilloscope);
		vca.getOutput().connect(speaker);
	}

	/**
	 * @throws FileNotFoundException
	 * @throws LineUnavailableException
	 */
	public static void testFiltrePasseBas() throws IOException, LineUnavailableException {

		var speaker = new Speaker("hauts-parleurs");
		var mp3FilePlayer = new Mp3FilePlayer("Lecteur de fichier MP3", TEST_MP3_PATH);
		var leftFilter = new LowPassVCF("filtre passe-bas gauche");
		var rightFilter = new LowPassVCF("filtre passe-bas droit");

		mp3FilePlayer.getOutputs().get(0).connect(leftFilter.getInput());
		mp3FilePlayer.getOutputs().get(1).connect(rightFilter.getInput());

		leftFilter.getOutput().connect(speaker.getInputs().get(0));
		rightFilter.getOutput().connect(speaker.getInputs().get(1));
	}

	/**
	 * @throws LineUnavailableException
	 * @throws FileNotFoundException
	 */
	public static void testFiltrePasseBande() throws LineUnavailableException, IOException {

		var speaker = new Speaker("Hauts-parleurs");
		var mp3FilePlayer = new Mp3FilePlayer("Lecteur de fichier MP3", TEST_MP3_PATH);
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

		var outputs = mp3FilePlayer.getOutputs();

		outputs.get(0).connect(highFilterLeft.getInput());
		outputs.get(1).connect(highFilterRight.getInput());
		highFilterLeft.getOutput().connect(lowFilterLeft.getInput());
		highFilterRight.getOutput().connect(lowFilterRight.getInput());
		lowFilterLeft.getOutput().connect(speaker.getInputs().get(0));
		lowFilterRight.getOutput().connect(speaker.getInputs().get(1));
		lowFilterLeft.getOutput().connect(spectrumAnalyzerFilterLeft.getInput());
		lowFilterRight.getOutput().connect(spectrumAnalyzerFilterRight.getInput());

		outputs.get(0).connect(spectrumAnalyzerLeft.getInput());
		outputs.get(1).connect(spectrumAnalyzerRight.getInput());
		outputs.get(0).connect(oscilloscopeLeft.getInput());
		outputs.get(1).connect(oscilloscopeRight.getInput());
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
		var speaker = new Speaker("speaker");
		var leftSpectrumAnalyzer = new SpectrumAnalyzer("left_spectrum_analyzer");
		var rightSpectrumAnalyzer = new SpectrumAnalyzer("right_spectrum_analyzer");

		microphone.connect(speaker);
		microphone.getOutputs().get(0).connect(leftSpectrumAnalyzer);
		microphone.getOutputs().get(1).connect(rightSpectrumAnalyzer);
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
	 * @throws CorruptWavFileException
	 */
	public static void testWav() throws IOException, LineUnavailableException, CorruptWavFileException {

		var speaker = new Speaker("speaker");
		var waveFilePlayer = new WavFilePlayer("wav_player", new File("sample.wav"));
		var oscilloscopeLeft = new Oscilloscope("left_oscilloscope");
		var oscilloscopeRight = new Oscilloscope("right_oscilloscope");
		var spectrumAnalyzerLeft = new SpectrumAnalyzer("left_spectrum_analyzer");
		var spectrumAnalyzerRight = new SpectrumAnalyzer("right_spectrum_analyzer");

		waveFilePlayer.connect(speaker);
		waveFilePlayer.getOutputs().get(0).connect(oscilloscopeLeft);
		waveFilePlayer.getOutputs().get(1).connect(oscilloscopeRight);
		waveFilePlayer.getOutputs().get(0).connect(spectrumAnalyzerLeft);
		waveFilePlayer.getOutputs().get(1).connect(spectrumAnalyzerRight);
	}

	/**
	 * @throws LineUnavailableException
	 * @throws IOException
	 * @throws CorruptWavFileException
	 */
	public static void montage2() throws LineUnavailableException, IOException, CorruptWavFileException {

		var speaker = new Speaker("Hauts-parleurs");
		var wavFilePlayer = new WavFilePlayer("Lecteur de fichier WAV", new File("sample.wav"));
		var spectrumAnalyzerLeft = new SpectrumAnalyzer("Analyseur de spectre, gauche");
		var spectrumAnalyzerRight = new SpectrumAnalyzer("Analyseur de spectre, droit");
		var oscilloscopeLeft = new Oscilloscope("Oscilloscope, gauche");
		var oscilloscopeRight = new Oscilloscope("Oscilloscope, droit");

		wavFilePlayer.connect(speaker);

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
		var vco = new VCO("vco", 440);
		var lfoFilter = new LFO("lfo_vcf", 1);
		var highPassFilter = new HighPassVCF("highpass_vcf");
		var lowPassFilter = new LowPassVCF("lowpass_vcf");
		var spectrumAnalyzer = new SpectrumAnalyzer("spectrum_analyzer");
		var oscilloscope = new Oscilloscope("oscilloscope");
		var oscilloscopeVco = new Oscilloscope("oscilloscope_vco");

		lfo.connect(vco);
		vco.connect(lowPassFilter);
		vco.connect(oscilloscopeVco);
		lowPassFilter.connect(highPassFilter);
		highPassFilter.getOutput().connect(speaker);
		highPassFilter.getOutput().connect(spectrumAnalyzer);
		highPassFilter.getOutput().connect(oscilloscope);
		lfoFilter.getOutput().connect(lowPassFilter.getModulation());
		lfoFilter.getOutput().connect(highPassFilter.getModulation());
	}

	/**
	 * @throws LineUnavailableException
	 */
	public static void montageConsonance() throws LineUnavailableException {

		var speaker = new Speaker("Hauts-parleurs");
		var vco1 = new VCO("VCO1", 440);
		var vco2 = new VCO("VCO2", 220);

		vco1.getOutput().connect(speaker.getInputs().get(0));
		vco2.getOutput().connect(speaker.getInputs().get(1));
	}
}
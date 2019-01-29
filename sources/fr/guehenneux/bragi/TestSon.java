package fr.guehenneux.bragi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.sound.sampled.LineUnavailableException;

import fr.guehenneux.bragi.module.*;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.jlp;

/**
 * @author Jonathan Guéhenneux
 */
public class TestSon {

	/**
	 * @param args
	 * @throws LineUnavailableException
	 * @throws CorruptWavFileException
	 * @throws IOException
	 * @throws JavaLayerException
	 */
	public static void main(String[] args) throws LineUnavailableException, IOException, CorruptWavFileException,
			JavaLayerException {

		montage4();
	}

	/**
	 * Modules:
	 * <ul>
	 *   <li>VCO</li>
	 *   <li>spectrum analyzer</li>
	 *   <li>stereo speaker</li>
	 * </ul>
	 *
	 * Connections:
	 * <ul>
	 *   <li>VCO -> spectrum analyzer</li>
	 *   <li>VCO -> left speaker</li>
	 *   <li>VCO -> right speaker</li>
	 * </ul>
	 */
	public static void montage3() throws LineUnavailableException {

		VCO vco = new VCO("VCO", 440);
		Speaker speaker = new Speaker("speaker");
		SpectrumAnalyzer spectrumAnalyzer = new SpectrumAnalyzer("spectrumAnalyzer");

		vco.connect(spectrumAnalyzer);
		vco.getOutput().connect(speaker);
	}

	public static void montage4() throws LineUnavailableException {

		Microphone microphone = new Microphone("microphone");
		Sampler leftSampler = new Sampler("left_sampler");
		Sampler rightSampler = new Sampler("right_sampler");
		Speaker speaker = new Speaker("speaker");

		microphone.getOutputs().get(0).connect(leftSampler);
		microphone.getOutputs().get(1).connect(rightSampler);
		leftSampler.getOutput().connect(speaker.getInputs().get(0));
		rightSampler.getOutput().connect(speaker.getInputs().get(1));
	}

	public static void montage5() throws FileNotFoundException, LineUnavailableException {

		Mp3FilePlayer player = new Mp3FilePlayer("mp3_player", new File(
				"G:/Musique/Aaliyah/Aaliyah (2001)/15. Try Again.mp3"));

		Sampler leftSampler = new Sampler("left_sampler");
		Sampler rightSampler = new Sampler("right_sampler");
		Speaker speaker = new Speaker("speaker");

		player.getOutputs().get(0).connect(leftSampler);
		player.getOutputs().get(1).connect(rightSampler);
		leftSampler.getOutput().connect(speaker.getInputs().get(0));
		rightSampler.getOutput().connect(speaker.getInputs().get(1));
	}

	public static void montage6() throws FileNotFoundException, LineUnavailableException {

		VCO vco = new VCO("vco", 440);
		Oscilloscope oscilloscope = new Oscilloscope("oscilloscope");
		vco.connect(oscilloscope);
		Speaker speaker = new Speaker("speaker");
		vco.getOutput().connect(speaker);
	}

	public static void montage7() throws FileNotFoundException, LineUnavailableException {

		Keyboard keyboard = new Keyboard("keyboard");
		Speaker speaker = new Speaker("speaker");
		keyboard.getOutput().connect(speaker);
	}

	/**
	 * @throws FileNotFoundException
	 * @throws LineUnavailableException
	 */
	public static void testFiltrePasseBas() throws FileNotFoundException, LineUnavailableException {

		Speaker speaker = new Speaker("hauts-parleurs");

		Mp3FilePlayer mp3FilePlayer = new Mp3FilePlayer("Lecteur de fichier MP3", new File(
				"G:/Musique/Aaliyah/Aaliyah (2001)/15. Try Again.mp3"));

		VCF leftFilter = new LowPassVCF("filtre passe-bas gauche");
		VCF rightFilter = new LowPassVCF("filtre passe-bas droit");

		mp3FilePlayer.getOutputs().get(0).connect(leftFilter.getInput());
		mp3FilePlayer.getOutputs().get(1).connect(rightFilter.getInput());

		leftFilter.getOutput().connect(speaker.getInputs().get(0));
		rightFilter.getOutput().connect(speaker.getInputs().get(1));
	}

	/**
	 * @throws LineUnavailableException
	 * @throws FileNotFoundException
	 */
	public static void testFiltrePasseBande() throws LineUnavailableException, FileNotFoundException {

		Speaker speaker = new Speaker("Hauts-parleurs");

		Mp3FilePlayer mp3FilePlayer = new Mp3FilePlayer("Lecteur de fichier MP3", new File(
				"G:/Musique/Aaliyah/Aaliyah (2001)/15. Try Again.mp3"));

		SpectrumAnalyzer spectrumAnalyzerLeft = new SpectrumAnalyzer("Analyseur de spectre, gauche");
		SpectrumAnalyzer spectrumAnalyzerRight = new SpectrumAnalyzer("Analyseur de spectre, droit");
		Oscilloscope oscilloscopeLeft = new Oscilloscope("Oscilloscope, gauche");
		Oscilloscope oscilloscopeRight = new Oscilloscope("Oscilloscope, droit");
		VCF highFilterLeft = new HighPassVCF("Filtre passe-haut, gauche");
		VCF highFilterRight = new HighPassVCF("Filtre passe-haut, droit");
		VCF lowFilterLeft = new LowPassVCF("Filtre passe-bas, gauche");
		VCF lowFilterRight = new LowPassVCF("Filtre passe-bas, droit");

		SpectrumAnalyzer spectrumAnalyzerFilterLeft = new SpectrumAnalyzer("Analyseur de spectre filtré, gauche");
		SpectrumAnalyzer spectrumAnalyzerFilterRight = new SpectrumAnalyzer("Analyseur de spectre filtré, droit");

		lowFilterLeft.setCutOffFrequency(220);
		lowFilterRight.setCutOffFrequency(220);
		highFilterLeft.setCutOffFrequency(880);
		highFilterRight.setCutOffFrequency(880);

		List<Output> outputs = mp3FilePlayer.getOutputs();

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

	public static void testFiltrePasseHaut() throws LineUnavailableException, FileNotFoundException {

		Speaker speaker = new Speaker("Hauts-parleurs");

		Mp3FilePlayer mp3FilePlayer = new Mp3FilePlayer("Lecteur de fichier MP3", new File(
				"G:/Musique/Aaliyah/Aaliyah (2001)/15. Try Again.mp3"));

		SpectrumAnalyzer spectrumAnalyzerLeft = new SpectrumAnalyzer("Analyseur de spectre, gauche");
		SpectrumAnalyzer spectrumAnalyzerRight = new SpectrumAnalyzer("Analyseur de spectre, droit");
		Oscilloscope oscilloscopeLeft = new Oscilloscope("Oscilloscope, gauche");
		Oscilloscope oscilloscopeRight = new Oscilloscope("Oscilloscope, droit");
		VCF filterLeft = new HighPassVCF("Filtre passe-haut, gauche");
		VCF filterRight = new HighPassVCF("Filtre passe-haut, droit");
		SpectrumAnalyzer spectrumAnalyzerFilterLeft = new SpectrumAnalyzer("Analyseur de spectre filtré, gauche");
		SpectrumAnalyzer spectrumAnalyzerFilterRight = new SpectrumAnalyzer("Analyseur de spectre filtré, droit");

		filterLeft.setCutOffFrequency(5000);
		filterRight.setCutOffFrequency(5000);

		List<Output> outputs = mp3FilePlayer.getOutputs();

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

	public static void lireMP3() throws FileNotFoundException, LineUnavailableException, JavaLayerException {

		jlp player = jlp.createInstance(new String[] { "E:/Musique/Aaliyah/Aaliyah (2001)/15. Try Again.mp3" });

		player.play();
	}

	public static void testMicro() throws LineUnavailableException {

		Microphone microphone = new Microphone("microphone");
		Speaker speaker = new Speaker("speaker");
		SpectrumAnalyzer leftSpectrumAnalyzer = new SpectrumAnalyzer("left_spectrum_analyzer");
		SpectrumAnalyzer rightSpectrumAnalyzer = new SpectrumAnalyzer("right_spectrum_analyzer");
		microphone.connect(speaker);
		microphone.getOutputs().get(0).connect(leftSpectrumAnalyzer);
		microphone.getOutputs().get(1).connect(rightSpectrumAnalyzer);
	}

	public static void testMP3() throws FileNotFoundException, LineUnavailableException {

		Speaker speaker = new Speaker("speaker");

		Mp3FilePlayer player = new Mp3FilePlayer("mp3_player", new File(
				"G:\\Musique\\Suzanne Vega\\Retrospective - The Best Of (2003)\\CD2\\08. Tom'S Diner (Original Version).mp3"));

		Oscilloscope oscilloscopeLeft = new Oscilloscope("left_oscilloscope");
		Oscilloscope oscilloscopeRight = new Oscilloscope("right_oscilloscope");

		SpectrumAnalyzer spectrumAnalyzerLeft = new SpectrumAnalyzer("left_spectrum_analyzer");
		SpectrumAnalyzer spectrumAnalyzerRight = new SpectrumAnalyzer("right_spectrum_analyzer");

		List<Output> outputs = player.getOutputs();

		outputs.get(0).connect(speaker.getInputs().get(0));
		outputs.get(1).connect(speaker.getInputs().get(1));
		outputs.get(0).connect(spectrumAnalyzerLeft.getInput());
		outputs.get(1).connect(spectrumAnalyzerRight.getInput());
		outputs.get(0).connect(oscilloscopeLeft.getInput());
		outputs.get(1).connect(oscilloscopeRight.getInput());
	}

	public static void testWav() throws IOException, LineUnavailableException, CorruptWavFileException {

		Speaker speaker = new Speaker("Hauts-parleurs");

		WavFilePlayer waveFilePlayer = new WavFilePlayer("Lecteur de fichier WAV", new File(
				"C:\\Users\\guehenneux\\Downloads\\a2002011001-e02.wav"));

		Oscilloscope oscilloscopeLeft = new Oscilloscope("Oscilloscope, gauche");
		Oscilloscope oscilloscopeRight = new Oscilloscope("Oscilloscope, droit");

		SpectrumAnalyzer spectrumAnalyzerLeft = new SpectrumAnalyzer("SpectrumAnalyzer, gauche");
		SpectrumAnalyzer spectrumAnalyzerRight = new SpectrumAnalyzer("SpectrumAnalyzer, droit");

		List<Output> outputs = waveFilePlayer.getOutputs();

		outputs.get(0).connect(speaker.getInputs().get(0));
		outputs.get(1).connect(speaker.getInputs().get(1));
		outputs.get(0).connect(spectrumAnalyzerLeft.getInput());
		outputs.get(1).connect(spectrumAnalyzerRight.getInput());
		outputs.get(0).connect(oscilloscopeLeft.getInput());
		outputs.get(1).connect(oscilloscopeRight.getInput());
	}

	public static void montage2() throws LineUnavailableException, IOException, CorruptWavFileException {

		Speaker speaker = new Speaker("Hauts-parleurs");

		WavFilePlayer wavFilePlayer = new WavFilePlayer("Lecteur de fichier WAV", new File("H:/Money.wav"));

		SpectrumAnalyzer spectrumAnalyzerLeft = new SpectrumAnalyzer("Analyseur de spectre, gauche");
		SpectrumAnalyzer spectrumAnalyzerRight = new SpectrumAnalyzer("Analyseur de spectre, droit");
		Oscilloscope oscilloscopeLeft = new Oscilloscope("Oscilloscope, gauche");
		Oscilloscope oscilloscopeRight = new Oscilloscope("Oscilloscope, droit");

		wavFilePlayer.connect(speaker);

		List<Output> outputs = wavFilePlayer.getOutputs();
		outputs.get(0).connect(spectrumAnalyzerLeft.getInput());
		outputs.get(1).connect(spectrumAnalyzerRight.getInput());
		outputs.get(0).connect(oscilloscopeLeft.getInput());
		outputs.get(1).connect(oscilloscopeRight.getInput());
	}

	public static void montage1() throws LineUnavailableException {

		Speaker speaker = new Speaker("Hauts-parleurs");

		LFO lfo = new LFO("LFO", 1);
		VCO vco = new VCO("VCO", 440);

		VCF highFilter = new HighPassVCF("Filtre passe-haut");
		VCF lowFilter = new LowPassVCF("Filtre passe-bas");

		lowFilter.setCutOffFrequency(220);
		highFilter.setCutOffFrequency(880);

		SpectrumAnalyzer spectrumAnalyzerVCO = new SpectrumAnalyzer("Analyseur de spectre");
		Oscilloscope oscilloscopeVCO = new Oscilloscope("Oscilloscope");

		lfo.getOutput().connect(vco.getModulationPort());
		vco.getOutput().connect(lowFilter.getInput());
		lowFilter.getOutput().connect(highFilter.getInput());
		highFilter.getOutput().connect(speaker.getInputs().get(0));
		highFilter.getOutput().connect(speaker.getInputs().get(1));
		highFilter.getOutput().connect(spectrumAnalyzerVCO.getInput());
		highFilter.getOutput().connect(oscilloscopeVCO.getInput());
	}

	public static void montageConsonance() throws LineUnavailableException {

		Speaker speaker = new Speaker("Hauts-parleurs");
		VCO vco1 = new VCO("VCO1", 440);
		VCO vco2 = new VCO("VCO2", 220);
		vco1.getOutput().connect(speaker.getInputs().get(0));
		vco2.getOutput().connect(speaker.getInputs().get(1));
	}
}
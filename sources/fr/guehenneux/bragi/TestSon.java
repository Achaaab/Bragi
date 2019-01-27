package fr.guehenneux.bragi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

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

		montage3();
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
		Spectrum spectrum = new Spectrum("spectrum");

		vco.getOutputPort().connect(spectrum.getInputPort());
		vco.getOutputPort().connect(speaker.getInputPorts()[0]);
		vco.getOutputPort().connect(speaker.getInputPorts()[1]);

		vco.start();
		spectrum.start();
		speaker.start();
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

		mp3FilePlayer.getOutputs()[0].connect(leftFilter.getInput());
		mp3FilePlayer.getOutputs()[1].connect(rightFilter.getInput());

		leftFilter.getOutput().connect(speaker.getInputs()[0]);
		rightFilter.getOutput().connect(speaker.getInputs()[1]);

		speaker.start();
		mp3FilePlayer.start();
		leftFilter.start();
		rightFilter.start();
	}

	/**
	 * @throws LineUnavailableException
	 * @throws FileNotFoundException
	 */
	public static void testFiltrePasseBande() throws LineUnavailableException, FileNotFoundException {

		Speaker speaker = new Speaker("Hauts-parleurs");

		Mp3FilePlayer mp3FilePlayer = new Mp3FilePlayer("Lecteur de fichier MP3", new File(
				"G:/Musique/Aaliyah/Aaliyah (2001)/15. Try Again.mp3"));

		Spectrum spectrumLeft = new Spectrum("Analyseur de spectre, gauche");
		Spectrum spectrumRight = new Spectrum("Analyseur de spectre, droit");
		Oscilloscope oscilloscopeLeft = new Oscilloscope("Oscilloscope, gauche");
		Oscilloscope oscilloscopeRight = new Oscilloscope("Oscilloscope, droit");
		VCF highFilterLeft = new HighPassVCF("Filtre passe-haut, gauche");
		VCF highFilterRight = new HighPassVCF("Filtre passe-haut, droit");
		VCF lowFilterLeft = new LowPassVCF("Filtre passe-bas, gauche");
		VCF lowFilterRight = new LowPassVCF("Filtre passe-bas, droit");

		Spectrum spectrumFilterLeft = new Spectrum("Analyseur de spectre filtré, gauche");
		Spectrum spectrumFilterRight = new Spectrum("Analyseur de spectre filtré, droit");

		lowFilterLeft.setCutOffFrequency(220);
		lowFilterRight.setCutOffFrequency(220);
		highFilterLeft.setCutOffFrequency(880);
		highFilterRight.setCutOffFrequency(880);

		Output[] outputs = mp3FilePlayer.getOutputs();

		outputs[0].connect(highFilterLeft.getInput());
		outputs[1].connect(highFilterRight.getInput());
		highFilterLeft.getOutput().connect(lowFilterLeft.getInput());
		highFilterRight.getOutput().connect(lowFilterRight.getInput());
		lowFilterLeft.getOutput().connect(speaker.getInputs()[0]);
		lowFilterRight.getOutput().connect(speaker.getInputs()[1]);
		lowFilterLeft.getOutput().connect(spectrumFilterLeft.getInput());
		lowFilterRight.getOutput().connect(spectrumFilterRight.getInput());

		outputs[0].connect(spectrumLeft.getInput());
		outputs[1].connect(spectrumRight.getInput());
		outputs[0].connect(oscilloscopeLeft.getInput());
		outputs[1].connect(oscilloscopeRight.getInput());

		speaker.start();
		highFilterLeft.start();
		highFilterRight.start();
		spectrumLeft.start();
		spectrumRight.start();
		oscilloscopeLeft.start();
		oscilloscopeRight.start();
		mp3FilePlayer.start();
		spectrumFilterLeft.start();
		spectrumFilterRight.start();
		lowFilterLeft.start();
		lowFilterRight.start();
	}

	public static void testFiltrePasseHaut() throws LineUnavailableException, FileNotFoundException {

		Speaker speaker = new Speaker("Hauts-parleurs");

		Mp3FilePlayer mp3FilePlayer = new Mp3FilePlayer("Lecteur de fichier MP3", new File(
				"G:/Musique/Aaliyah/Aaliyah (2001)/15. Try Again.mp3"));

		Spectrum spectrumLeft = new Spectrum("Analyseur de spectre, gauche");
		Spectrum spectrumRight = new Spectrum("Analyseur de spectre, droit");
		Oscilloscope oscilloscopeLeft = new Oscilloscope("Oscilloscope, gauche");
		Oscilloscope oscilloscopeRight = new Oscilloscope("Oscilloscope, droit");
		VCF filterLeft = new HighPassVCF("Filtre passe-haut, gauche");
		VCF filterRight = new HighPassVCF("Filtre passe-haut, droit");
		Spectrum spectrumFilterLeft = new Spectrum("Analyseur de spectre filtré, gauche");
		Spectrum spectrumFilterRight = new Spectrum("Analyseur de spectre filtré, droit");

		filterLeft.setCutOffFrequency(5000);
		filterRight.setCutOffFrequency(5000);

		Output[] outputs = mp3FilePlayer.getOutputs();

		outputs[0].connect(filterLeft.getInput());
		outputs[1].connect(filterRight.getInput());
		filterLeft.getOutput().connect(speaker.getInputs()[0]);
		filterRight.getOutput().connect(speaker.getInputs()[1]);
		filterLeft.getOutput().connect(spectrumFilterLeft.getInput());
		filterRight.getOutput().connect(spectrumFilterRight.getInput());

		outputs[0].connect(spectrumLeft.getInput());
		outputs[1].connect(spectrumRight.getInput());
		outputs[0].connect(oscilloscopeLeft.getInput());
		outputs[1].connect(oscilloscopeRight.getInput());

		speaker.start();
		filterLeft.start();
		filterRight.start();
		spectrumLeft.start();
		spectrumRight.start();
		oscilloscopeLeft.start();
		oscilloscopeRight.start();
		mp3FilePlayer.start();
		spectrumFilterLeft.start();
		spectrumFilterRight.start();
	}

	public static void lireMP3() throws FileNotFoundException, LineUnavailableException, JavaLayerException {

		jlp player = jlp.createInstance(new String[] { "E:/Musique/Aaliyah/Aaliyah (2001)/15. Try Again.mp3" });

		player.play();
	}

	public static void testMicro() throws LineUnavailableException {

		Micro micro = new Micro("Entrée microphone");
		Speaker speaker = new Speaker("Hauts-parleurs");
		LowPassVCF leftFilter = new LowPassVCF("Filtre passe-bas, gauche");
		LowPassVCF rightFilter = new LowPassVCF("Filtre passe-bas, droit");
		Spectrum leftSpectrum = new Spectrum("Analyseur de spectre, gauche");
		Spectrum rightSpectrum = new Spectrum("Analyseur de spectre, droit");

		micro.getOutputs()[0].connect(leftFilter.getInput());
		micro.getOutputs()[1].connect(rightFilter.getInput());

		leftFilter.getOutput().connect(speaker.getInputs()[0]);
		rightFilter.getOutput().connect(speaker.getInputs()[1]);
		leftFilter.getOutput().connect(leftSpectrum.getInput());
		rightFilter.getOutput().connect(rightSpectrum.getInput());

		micro.start();
		leftFilter.start();
		rightFilter.start();
		speaker.start();
		leftSpectrum.start();
		rightSpectrum.start();
	}

	public static void testMP3() throws FileNotFoundException, LineUnavailableException {

		Speaker speaker = new Speaker("Hauts-parleurs");

		Mp3FilePlayer mp3FilePlayer = new Mp3FilePlayer("Lecteur de fichier MP3", new File(
				"G:\\Musique\\Rage Against The Machine\\Battle Of Los Angeles (1999)\\03. Calm Like A Bomb.mp3"));

		Oscilloscope oscilloscopeLeft = new Oscilloscope("Oscilloscope, gauche");
		Oscilloscope oscilloscopeRight = new Oscilloscope("Oscilloscope, droit");

		Spectrum spectrumLeft = new Spectrum("Spectrum, gauche");
		Spectrum spectrumRight = new Spectrum("Spectrum, droit");

		Output[] outputs = mp3FilePlayer.getOutputs();

		outputs[0].connect(speaker.getInputs()[0]);
		outputs[1].connect(speaker.getInputs()[1]);
		outputs[0].connect(spectrumLeft.getInput());
		outputs[1].connect(spectrumRight.getInput());
		outputs[0].connect(oscilloscopeLeft.getInput());
		outputs[1].connect(oscilloscopeRight.getInput());

		speaker.start();
		oscilloscopeLeft.start();
		oscilloscopeRight.start();
		mp3FilePlayer.start();
		spectrumLeft.start();
		spectrumRight.start();
	}

	public static void testWav() throws IOException, LineUnavailableException, CorruptWavFileException {

		Speaker speaker = new Speaker("Hauts-parleurs");

		WavFilePlayer waveFilePlayer = new WavFilePlayer("Lecteur de fichier WAV", new File(
				"C:\\Users\\guehenneux\\Downloads\\a2002011001-e02.wav"));

		Oscilloscope oscilloscopeLeft = new Oscilloscope("Oscilloscope, gauche");
		Oscilloscope oscilloscopeRight = new Oscilloscope("Oscilloscope, droit");

		Spectrum spectrumLeft = new Spectrum("Spectrum, gauche");
		Spectrum spectrumRight = new Spectrum("Spectrum, droit");

		Output[] outputs = waveFilePlayer.getOutputs();

		outputs[0].connect(speaker.getInputs()[0]);
		outputs[1].connect(speaker.getInputs()[1]);
		outputs[0].connect(spectrumLeft.getInput());
		outputs[1].connect(spectrumRight.getInput());
		outputs[0].connect(oscilloscopeLeft.getInput());
		outputs[1].connect(oscilloscopeRight.getInput());

		speaker.start();
		oscilloscopeLeft.start();
		oscilloscopeRight.start();
		waveFilePlayer.start();
		spectrumLeft.start();
		spectrumRight.start();
	}

	public static void montage2() throws LineUnavailableException, IOException, CorruptWavFileException {

		Speaker speaker = new Speaker("Hauts-parleurs");

		WavFilePlayer wavFilePlayer = new WavFilePlayer("Lecteur de fichier WAV", new File("H:/Money.wav"));

		Spectrum spectrumLeft = new Spectrum("Analyseur de spectre, gauche");
		Spectrum spectrumRight = new Spectrum("Analyseur de spectre, droit");
		Oscilloscope oscilloscopeLeft = new Oscilloscope("Oscilloscope, gauche");
		Oscilloscope oscilloscopeRight = new Oscilloscope("Oscilloscope, droit");

		Output[] outputs = wavFilePlayer.getOutputs();

		outputs[0].connect(speaker.getInputs()[0]);
		outputs[1].connect(speaker.getInputs()[1]);
		outputs[0].connect(spectrumLeft.getInput());
		outputs[1].connect(spectrumRight.getInput());
		outputs[0].connect(oscilloscopeLeft.getInput());
		outputs[1].connect(oscilloscopeRight.getInput());

		speaker.start();
		spectrumLeft.start();
		spectrumRight.start();
		oscilloscopeLeft.start();
		oscilloscopeRight.start();
		wavFilePlayer.start();
	}

	public static void montage1() throws LineUnavailableException {

		Speaker speaker = new Speaker("Hauts-parleurs");

		LFO lfo = new LFO("LFO", 1);
		VCO vco = new VCO("VCO", 440);

		VCF highFilter = new HighPassVCF("Filtre passe-haut");
		VCF lowFilter = new LowPassVCF("Filtre passe-bas");

		lowFilter.setCutOffFrequency(220);
		highFilter.setCutOffFrequency(880);

		Spectrum spectrumVCO = new Spectrum("Analyseur de spectre");
		Oscilloscope oscilloscopeVCO = new Oscilloscope("Oscilloscope");

		lfo.getOutput().connect(vco.getModulationPort());
		vco.getOutput().connect(lowFilter.getInput());
		lowFilter.getOutput().connect(highFilter.getInput());
		highFilter.getOutput().connect(speaker.getInputs()[0]);
		highFilter.getOutput().connect(speaker.getInputs()[1]);
		highFilter.getOutput().connect(spectrumVCO.getInput());
		highFilter.getOutput().connect(oscilloscopeVCO.getInput());

		lfo.start();
		vco.start();
		lowFilter.start();
		highFilter.start();
		speaker.start();
		spectrumVCO.start();
		oscilloscopeVCO.start();
	}

	public static void montageConsonance() throws LineUnavailableException {

		Speaker speaker = new Speaker("Hauts-parleurs");
		VCO vco1 = new VCO("VCO1", 440);
		VCO vco2 = new VCO("VCO2", 220);
		vco1.getOutput().connect(speaker.getInputs()[0]);
		vco2.getOutput().connect(speaker.getInputs()[1]);

		speaker.start();
		vco1.start();
		vco2.start();
	}
}
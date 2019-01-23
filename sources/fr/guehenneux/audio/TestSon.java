package fr.guehenneux.audio;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.jlp;

/**
 * @author GUEHENNEUX
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

		testWav();
	}

	/**
	 * @throws FileNotFoundException
	 * @throws LineUnavailableException
	 */
	public static void testFiltrePasseBas() throws FileNotFoundException, LineUnavailableException {

		Speaker speaker = new Speaker("hauts-parleurs");

		Mp3FilePlayer mp3FilePlayer = new Mp3FilePlayer("Lecteur de fichier MP3", new File(
				"E:/Musique/Aaliyah/Aaliyah (2001)/15. Try Again.mp3"));

		VCF leftFilter = new LowPassVCF("filtre passe-bas gauche");
		VCF rightFilter = new LowPassVCF("filtre passe-bas droit");

		mp3FilePlayer.getOutputPorts()[0].connect(leftFilter.getInputPort());
		mp3FilePlayer.getOutputPorts()[1].connect(rightFilter.getInputPort());

		leftFilter.getOutputPort().connect(speaker.getInputPorts()[0]);
		rightFilter.getOutputPort().connect(speaker.getInputPorts()[1]);

		Thread threadSpeaker = new ThreadModule(speaker);
		Thread threadPlayer = new ThreadModule(mp3FilePlayer);
		Thread threadLeftFilter = new ThreadModule(leftFilter);
		Thread threadRightFilter = new ThreadModule(rightFilter);

		threadSpeaker.start();
		threadPlayer.start();
		threadLeftFilter.start();
		threadRightFilter.start();
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
		Oscillo oscilloLeft = new Oscillo("Oscilloscope, gauche");
		Oscillo oscilloRight = new Oscillo("Oscilloscope, droit");
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

		OutputPort[] outputPorts = mp3FilePlayer.getOutputPorts();

		outputPorts[0].connect(highFilterLeft.getInputPort());
		outputPorts[1].connect(highFilterRight.getInputPort());
		highFilterLeft.getOutputPort().connect(lowFilterLeft.getInputPort());
		highFilterRight.getOutputPort().connect(lowFilterRight.getInputPort());
		lowFilterLeft.getOutputPort().connect(speaker.getInputPorts()[0]);
		lowFilterRight.getOutputPort().connect(speaker.getInputPorts()[1]);
		lowFilterLeft.getOutputPort().connect(spectrumFilterLeft.getInputPort());
		lowFilterRight.getOutputPort().connect(spectrumFilterRight.getInputPort());

		outputPorts[0].connect(spectrumLeft.getInputPort());
		outputPorts[1].connect(spectrumRight.getInputPort());
		outputPorts[0].connect(oscilloLeft.getPortEntree());
		outputPorts[1].connect(oscilloRight.getPortEntree());

		Thread threadSpeaker = new ThreadModule(speaker);
		Thread threadHighFilterLeft = new ThreadModule(highFilterLeft);
		Thread threadHighFilterRight = new ThreadModule(highFilterRight);
		Thread threadLowFilterLeft = new ThreadModule(lowFilterLeft);
		Thread threadLowFilterRight = new ThreadModule(lowFilterRight);
		Thread threadPlayer = new ThreadModule(mp3FilePlayer);
		Thread threadSpectrumLeft = new ThreadModule(spectrumLeft);
		Thread threadSpectrumRight = new ThreadModule(spectrumRight);
		Thread threadOscilloLeft = new ThreadModule(oscilloLeft);
		Thread threadOscilloRight = new ThreadModule(oscilloRight);
		Thread threadSpectrumFilterLeft = new ThreadModule(spectrumFilterLeft);
		Thread threadSpectrumFilterRight = new ThreadModule(spectrumFilterRight);

		threadSpeaker.start();
		threadHighFilterLeft.start();
		threadHighFilterRight.start();
		threadSpectrumLeft.start();
		threadSpectrumRight.start();
		threadOscilloLeft.start();
		threadOscilloRight.start();
		threadPlayer.start();
		threadSpectrumFilterLeft.start();
		threadSpectrumFilterRight.start();
		threadLowFilterLeft.start();
		threadLowFilterRight.start();

	}

	public static void testFiltrePasseHaut() throws LineUnavailableException, FileNotFoundException {

		Speaker speaker = new Speaker("Hauts-parleurs");

		Mp3FilePlayer mp3FilePlayer = new Mp3FilePlayer("Lecteur de fichier MP3", new File(
				"E:/Musique/Aaliyah/Aaliyah (2001)/15. Try Again.mp3"));

		Spectrum spectrumLeft = new Spectrum("Analyseur de spectre, gauche");
		Spectrum spectrumRight = new Spectrum("Analyseur de spectre, droit");
		Oscillo oscilloLeft = new Oscillo("Oscilloscope, gauche");
		Oscillo oscilloRight = new Oscillo("Oscilloscope, droit");
		VCF filterLeft = new HighPassVCF("Filtre passe-haut, gauche");
		VCF filterRight = new HighPassVCF("Filtre passe-haut, droit");
		Spectrum spectrumFilterLeft = new Spectrum("Analyseur de spectre filtr�, gauche");
		Spectrum spectrumFilterRight = new Spectrum("Analyseur de spectre filtr�, droit");

		filterLeft.setCutOffFrequency(5000);
		filterRight.setCutOffFrequency(5000);

		OutputPort[] outputPorts = mp3FilePlayer.getOutputPorts();

		outputPorts[0].connect(filterLeft.getInputPort());
		outputPorts[1].connect(filterRight.getInputPort());
		filterLeft.getOutputPort().connect(speaker.getInputPorts()[0]);
		filterRight.getOutputPort().connect(speaker.getInputPorts()[1]);
		filterLeft.getOutputPort().connect(spectrumFilterLeft.getInputPort());
		filterRight.getOutputPort().connect(spectrumFilterRight.getInputPort());

		outputPorts[0].connect(spectrumLeft.getInputPort());
		outputPorts[1].connect(spectrumRight.getInputPort());
		outputPorts[0].connect(oscilloLeft.getPortEntree());
		outputPorts[1].connect(oscilloRight.getPortEntree());

		Thread threadSpeaker = new ThreadModule(speaker);
		Thread threadFilterLeft = new ThreadModule(filterLeft);
		Thread threadFilterRight = new ThreadModule(filterRight);
		Thread threadPlayer = new ThreadModule(mp3FilePlayer);
		Thread threadSpectrumLeft = new ThreadModule(spectrumLeft);
		Thread threadSpectrumRight = new ThreadModule(spectrumRight);
		Thread threadOscilloLeft = new ThreadModule(oscilloLeft);
		Thread threadOscilloRight = new ThreadModule(oscilloRight);
		Thread threadSpectrumFilterLeft = new ThreadModule(spectrumFilterLeft);
		Thread threadSpectrumFilterRight = new ThreadModule(spectrumFilterRight);

		threadSpeaker.start();
		threadFilterLeft.start();
		threadFilterRight.start();
		threadSpectrumLeft.start();
		threadSpectrumRight.start();
		threadOscilloLeft.start();
		threadOscilloRight.start();
		threadPlayer.start();
		threadSpectrumFilterLeft.start();
		threadSpectrumFilterRight.start();
	}

	public static void lireMP3() throws FileNotFoundException, LineUnavailableException, JavaLayerException {

		jlp player = jlp.createInstance(new String[] { "E:/Musique/Aaliyah/Aaliyah (2001)/15. Try Again.mp3" });

		player.play();
	}

	public static void testMicro() throws LineUnavailableException {

		Micro micro = new Micro("Entrée microphone");
		Speaker speaker = new Speaker("Hauts-parleurs");
		Spectrum spectrum = new Spectrum("Analyseur de spectre");

		micro.getOutputPorts()[0].connect(speaker.getInputPorts()[0]);
		micro.getOutputPorts()[0].connect(spectrum.getInputPort());

		ThreadModule threadSpeeker = new ThreadModule(speaker);
		ThreadModule threadMicro = new ThreadModule(micro);
		ThreadModule threadSpectrum = new ThreadModule(spectrum);

		threadMicro.start();
		threadSpeeker.start();
		threadSpectrum.start();
	}

	public static void testMP3() throws FileNotFoundException, LineUnavailableException {

		Speaker speaker = new Speaker("Hauts-parleurs");

		Mp3FilePlayer mp3FilePlayer = new Mp3FilePlayer("Lecteur de fichier MP3", new File(
				"C:\\Users\\guehenneux\\Downloads\\14086.mp3"));

		Oscillo oscilloLeft = new Oscillo("Oscilloscope, gauche");
		Oscillo oscilloRight = new Oscillo("Oscilloscope, droit");

		Spectrum spectrumLeft = new Spectrum("Spectrum, gauche");
		Spectrum spectrumRight = new Spectrum("Spectrum, droit");

		OutputPort[] outputPorts = mp3FilePlayer.getOutputPorts();

		outputPorts[0].connect(speaker.getInputPorts()[0]);
		outputPorts[1].connect(speaker.getInputPorts()[1]);
		outputPorts[0].connect(spectrumLeft.getInputPort());
		outputPorts[1].connect(spectrumRight.getInputPort());
		outputPorts[0].connect(oscilloLeft.getPortEntree());
		outputPorts[1].connect(oscilloRight.getPortEntree());

		Thread threadSpeeker = new ThreadModule(speaker);
		Thread threadPlayer = new ThreadModule(mp3FilePlayer);
		Thread threadOscilloLeft = new ThreadModule(oscilloLeft);
		Thread threadOscilloRight = new ThreadModule(oscilloRight);
		Thread threadSpectrumLeft = new ThreadModule(spectrumLeft);
		Thread threadSpectrumRight = new ThreadModule(spectrumRight);

		threadSpeeker.start();
		threadOscilloLeft.start();
		threadOscilloRight.start();
		threadPlayer.start();
		threadSpectrumLeft.start();
		threadSpectrumRight.start();
	}

	public static void testWav() throws IOException, LineUnavailableException, CorruptWavFileException {

		Speaker speaker = new Speaker("Hauts-parleurs");

		WavFilePlayer waveFilePlayer = new WavFilePlayer("Lecteur de fichier WAV", new File(
				"C:\\Users\\guehenneux\\Downloads\\a2002011001-e02.wav"));

		Oscillo oscilloLeft = new Oscillo("Oscilloscope, gauche");
		Oscillo oscilloRight = new Oscillo("Oscilloscope, droit");

		Spectrum spectrumLeft = new Spectrum("Spectrum, gauche");
		Spectrum spectrumRight = new Spectrum("Spectrum, droit");

		OutputPort[] outputPorts = waveFilePlayer.getOutputPorts();

		outputPorts[0].connect(speaker.getInputPorts()[0]);
		outputPorts[1].connect(speaker.getInputPorts()[1]);
		outputPorts[0].connect(spectrumLeft.getInputPort());
		outputPorts[1].connect(spectrumRight.getInputPort());
		outputPorts[0].connect(oscilloLeft.getPortEntree());
		outputPorts[1].connect(oscilloRight.getPortEntree());

		Thread threadSpeeker = new ThreadModule(speaker);
		Thread threadPlayer = new ThreadModule(waveFilePlayer);
		Thread threadOscilloLeft = new ThreadModule(oscilloLeft);
		Thread threadOscilloRight = new ThreadModule(oscilloRight);
		Thread threadSpectrumLeft = new ThreadModule(spectrumLeft);
		Thread threadSpectrumRight = new ThreadModule(spectrumRight);

		threadSpeeker.start();
		threadOscilloLeft.start();
		threadOscilloRight.start();
		threadPlayer.start();
		threadSpectrumLeft.start();
		threadSpectrumRight.start();
	}

	public static void montage2() throws LineUnavailableException, IOException, CorruptWavFileException {

		Speaker speaker = new Speaker("Hauts-parleurs");

		WavFilePlayer wavFilePlayer = new WavFilePlayer("Lecteur de fichier WAV", new File("H:/Money.wav"));

		Spectrum spectrumLeft = new Spectrum("Analyseur de spectre, gauche");
		Spectrum spectrumRight = new Spectrum("Analyseur de spectre, droit");
		Oscillo oscilloLeft = new Oscillo("Oscilloscope, gauche");
		Oscillo oscilloRight = new Oscillo("Oscilloscope, droit");

		OutputPort[] outputPorts = wavFilePlayer.getOutputPorts();

		outputPorts[0].connect(speaker.getInputPorts()[0]);
		outputPorts[1].connect(speaker.getInputPorts()[1]);
		outputPorts[0].connect(spectrumLeft.getInputPort());
		outputPorts[1].connect(spectrumRight.getInputPort());
		outputPorts[0].connect(oscilloLeft.getPortEntree());
		outputPorts[1].connect(oscilloRight.getPortEntree());

		Thread threadSpeeker = new ThreadModule(speaker);
		Thread threadPlayer = new ThreadModule(wavFilePlayer);
		Thread threadSpectrumLeft = new ThreadModule(spectrumLeft);
		Thread threadSpectrumRight = new ThreadModule(spectrumRight);
		Thread threadOscilloLeft = new ThreadModule(oscilloLeft);
		Thread threadOscilloRight = new ThreadModule(oscilloRight);

		threadSpeeker.start();
		threadSpectrumLeft.start();
		threadSpectrumRight.start();
		threadOscilloLeft.start();
		threadOscilloRight.start();
		threadPlayer.start();

	}

	public static void montage1() throws LineUnavailableException {

		Speaker speaker = new Speaker("Hauts-parleurs");

		VCO vco = new VCO("VCO", 440);

		Spectrum spectrumVCO = new Spectrum("Analyseur de spectre");
		Oscillo oscilloVCO = new Oscillo("Oscilloscope");

		vco.getOutputPort().connect(speaker.getInputPorts()[0]);
		vco.getOutputPort().connect(speaker.getInputPorts()[1]);
		vco.getOutputPort().connect(spectrumVCO.getInputPort());
		vco.getOutputPort().connect(oscilloVCO.getPortEntree());

		ThreadModule threadSpeaker = new ThreadModule(speaker);
		ThreadModule threadVCO = new ThreadModule(vco);
		ThreadModule threadSpectrumVCO = new ThreadModule(spectrumVCO);
		ThreadModule threadOscilloVCO = new ThreadModule(oscilloVCO);

		threadVCO.start();
		threadSpeaker.start();
		threadSpectrumVCO.start();
		threadOscilloVCO.start();
	}

	public static void montageConsonance() throws LineUnavailableException {

		Speaker speaker = new Speaker("Hauts-parleurs");
		VCO vco1 = new VCO("VCO1", 440);
		VCO vco2 = new VCO("VCO2", 220);
		vco1.getOutputPort().connect(speaker.getInputPorts()[0]);
		vco2.getOutputPort().connect(speaker.getInputPorts()[1]);

		ThreadModule threadSpeaker = new ThreadModule(speaker);
		ThreadModule threadVCO1 = new ThreadModule(vco1);
		ThreadModule threadVCO2 = new ThreadModule(vco2);

		threadSpeaker.start();
		threadVCO1.start();
		threadVCO2.start();
	}
}
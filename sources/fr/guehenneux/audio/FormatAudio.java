package fr.guehenneux.audio;

/**
 * @author GUEHENNEUX
 */
public class FormatAudio {

	private static FormatAudio instance;

	private static final int DEFAULT_CHANNELS = 2;
	private static final int DEFAULT_SAMPLE_SIZE = 2;
	private static final int DEFAULT_FRAME_RATE = 44100;
	private static final int DEFAULT_BUFFER_SIZE_IN_FRAMES = 32;

	private int channels;
	private int sampleSize;
	private int frameRate;
	private int bufferSizeInFrames;

	/**
	 * @return
	 */
	public static FormatAudio getInstance() {

		if (instance == null) {
			instance = new FormatAudio();
		}

		return instance;
	}

	/**
	 * 
	 */
	private FormatAudio() {

		channels = DEFAULT_CHANNELS;
		frameRate = DEFAULT_FRAME_RATE;
		sampleSize = DEFAULT_SAMPLE_SIZE;
		bufferSizeInFrames = DEFAULT_BUFFER_SIZE_IN_FRAMES;
	}

	/**
	 * @return channels
	 */
	public int getChannels() {
		return channels;
	}

	/**
	 * @param channels
	 *            channels � d�finir
	 */
	public void setChannels(int channels) {
		this.channels = channels;
	}

	/**
	 * @return frameRate
	 */
	public int getFrameRate() {
		return frameRate;
	}

	/**
	 * @param frameRate
	 *            frameRate � d�finir
	 */
	public void setFrameRate(int frameRate) {
		this.frameRate = frameRate;
	}

	/**
	 * @return sampleSize
	 */
	public int getSampleSize() {
		return sampleSize;
	}

	/**
	 * @param sampleSize
	 *            sampleSize � d�finir
	 */
	public void setSampleSize(int sampleSize) {
		this.sampleSize = sampleSize;
	}

	/**
	 * @return bufferSizeInFrames
	 */
	public int getBufferSizeInFrames() {
		return bufferSizeInFrames;
	}

	/**
	 * @return
	 */
	public int getBufferSizeInSamples() {
		return bufferSizeInFrames * channels;
	}

	/**
	 * @param bufferSizeInFrames
	 *            bufferSizeInFrames � d�finir
	 */
	public void setBufferSizeInFrames(int bufferSizeInFrames) {
		this.bufferSizeInFrames = bufferSizeInFrames;
	}

	/**
	 * 
	 * @return
	 */
	public int getFrameSizeInBytes() {
		return channels * sampleSize;
	}

	/**
	 * @return
	 */
	public double getFrameLength() {
		return 1.0 / frameRate;
	}

	/**
	 * @return
	 */
	public double getBufferLength() {
		return bufferSizeInFrames / frameRate;
	}
}
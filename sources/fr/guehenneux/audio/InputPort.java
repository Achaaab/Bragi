package fr.guehenneux.audio;

/**
 * 
 * @author GUEHENNEUX
 *
 */
public class InputPort {

	private Buffer<float[]> inputBuffer;

	/**
     * 
     */
	public InputPort() {

	}

	/**
	 * @return inputBuffer
	 */
	public Buffer<float[]> getInputBuffer() {
		return inputBuffer;
	}

	/**
	 * @param inputBuffer
	 *            inputBuffer � d�finir
	 */
	public void setInputBuffer(Buffer<float[]> inputBuffer) {
		this.inputBuffer = inputBuffer;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isConnected() {
		return inputBuffer != null;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isReady() {
		return !inputBuffer.isEmpty();
	}

	/**
	 * 
	 * @return
	 */
	public float[] read() {
		return inputBuffer.get();
	}
}
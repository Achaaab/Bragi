package fr.guehenneux.audio;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jonathan Guéhenneux
 */
public class OutputPort {

	private List<Buffer<float[]>> outputBuffers;

	/**
	 *
	 */
	public OutputPort() {
		outputBuffers = new ArrayList<>();
	}

	/**
	 * @return outputBuffers
	 */
	public List<Buffer<float[]>> getOutputBuffers() {
		return outputBuffers;
	}

	/**
	 * @param outputBuffers
	 *            outputBuffers à définir
	 */
	public void setOutputBuffers(List<Buffer<float[]>> outputBuffers) {
		this.outputBuffers = outputBuffers;
	}

	/**
	 * @param data
	 */
	public synchronized void write(float[] data) {

		for (Buffer<float[]> outputBuffer : outputBuffers) {
			outputBuffer.ajouter(data);
		}
	}

	/**
	 * @return
	 */
	public boolean isConnected() {
		return !outputBuffers.isEmpty();
	}

	/**
	 * @param portEntree
	 */
	public synchronized void connect(InputPort portEntree) {

		Buffer<float[]> tampon = new Buffer<>(5);
		outputBuffers.add(tampon);
		portEntree.setInputBuffer(tampon);
	}
}
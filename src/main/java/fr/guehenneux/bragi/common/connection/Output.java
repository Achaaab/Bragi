package fr.guehenneux.bragi.common.connection;

/**
 * @author Jonathan Guéhenneux
 */
public interface Output {

	/**
	 * Connect this output to the specified input.
	 *
	 * @param input input to connect to
	 */
	void connect(Input input);

	/**
	 * @return whether this output is connected to at least 1 input
	 */
	boolean isConnected();

	/**
	 * Write a chunk to this output. Depending on the implementation, it is not guaranted that the chunk will be
	 * written.
	 *
	 * @param chunk chunk to write
	 * @throws InterruptedException if interrupted while writing chunk
	 */
	void write(float[] chunk) throws InterruptedException;
}
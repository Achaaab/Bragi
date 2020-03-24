package fr.guehenneux.bragi;

/**
 * @author Jonathan Guéhenneux
 */
public class MalformedWavFileException extends Exception {

	/**
	 * @param message exception message
	 */
	public MalformedWavFileException(String message) {
		super(message);
	}
}
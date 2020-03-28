package fr.guehenneux.bragi.common;

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
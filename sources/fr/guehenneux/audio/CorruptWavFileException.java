package fr.guehenneux.audio;

/**
 * @author Jonathan Guéhenneux
 */
public class CorruptWavFileException extends Exception {

    /**
     * @param message
     */
    public CorruptWavFileException(String message) {
        super(message);
    }
}
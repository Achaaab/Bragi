package fr.guehenneux.audio;

/**
 * 
 * @author GUEHENNEUX
 *
 */
public class CorruptWavFileException extends Exception {
    
    /**
     * 
     */
    private static final long serialVersionUID = -7996720745867554753L;

    /**
     * 
     * @param message
     */
    public CorruptWavFileException(String message) {
        super(message);
    }

}

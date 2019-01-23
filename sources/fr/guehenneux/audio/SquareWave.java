

package fr.guehenneux.audio;

/**
 * @author GUEHENNEUX
 */
public class SquareWave
    extends PulseWave {

    /**
     * @param frequency
     */
    public SquareWave(double frequency) {
        super(0.5f, frequency);
    }

}

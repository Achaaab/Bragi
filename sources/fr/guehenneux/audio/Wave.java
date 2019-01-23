

package fr.guehenneux.audio;

/**
 * @author GUEHENNEUX
 */
public interface Wave {

    /**
     * @return the frequency
     */
    double getFrequency();

    /**
     * @param frequency a frequency in the interval [30.0, 20000.0]
     */
    void setFrequency(double frequency);

    /**
     * @param sampleCount
     * @param sampleLength sample length in seconds (1/sample rate)
     * @return
     */
    float[] getSamples(int sampleCount, double sampleLength);

    /**
     * @param modulationSamples
     * @param sampleCount
     * @param sampleLength
     * @return
     */
    public float[] getSamples(float[] modulationSamples, int sampleCount, double sampleLength);
}
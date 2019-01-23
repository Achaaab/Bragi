

package fr.guehenneux.audio;

/**
 * @author GUEHENNEUX
 */
public class TriangleWave implements Wave {

    public static final double SINE_PERIOD = 2 * Math.PI;

    private double frequency;

    private double periodPercent;

    /**
     * @param frequency
     */
    public TriangleWave(double frequency) {

        this.frequency = frequency;
        periodPercent = 0;

    }

    public float[] getSamples(int sampleCount, double sampleLength) {

        float[] samples = new float[sampleCount];
        float sample;
        
        double period = 1.0 / frequency;
        
        for (int sampleIndex = 0; sampleIndex < sampleCount; sampleIndex++) {
            
            periodPercent %= 1;
            
            sample = periodPercent < 0.5 ?
                (float) (1 - 4 * periodPercent) :
                (float) (4 * periodPercent - 3);
                
            samples[sampleIndex] = sample;

            periodPercent += sampleLength / period;

        }

        return samples;

    }

    public float[] getSamples(float[] modulationSamples, int sampleCount, double sampleLength) {

        float[] samples = new float[sampleCount];
        float sample;
        
        float modulationSample;
        double modulationFactor;

        double period;

        for (int sampleIndex = 0; sampleIndex < sampleCount; sampleIndex++) {

            modulationSample = modulationSamples[sampleIndex];
            modulationFactor = Math.pow(2, modulationSample);

            period = 1.0 / frequency / modulationFactor;

            periodPercent %= 1;
            
            sample = (float) (2 * periodPercent - 1);
            samples[sampleIndex] = sample;

            periodPercent += sampleLength / period;

        }

        return samples;

    }

    public void setFrequency(double frequency) {
        this.frequency = frequency;
    }

    public double getFrequency() {
        return frequency;
    }

}

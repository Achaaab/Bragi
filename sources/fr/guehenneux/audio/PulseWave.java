

package fr.guehenneux.audio;

/**
 * @author GUEHENNEUX
 */
public class PulseWave implements Wave {
    
    private double frequency;

    private float pulsePercent;
    
    private double periodPercent;

    /**
     * 
     * @param pulsePercent
     * @param frequency
     */
    public PulseWave(float pulsePercent, double frequency) {

        this.frequency = frequency;
        this.pulsePercent = pulsePercent;
        periodPercent = 0;

    }

    public float[] getSamples(int sampleCount, double sampleLength) {

        float[] samples = new float[sampleCount];
        float sample;
        
        double period = 1.0 / frequency;
        
        for (int sampleIndex = 0; sampleIndex < sampleCount; sampleIndex++) {
            
            periodPercent %= 1;
            
            sample = periodPercent < pulsePercent ? 1.0f : -1.0f;
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
            
            sample = periodPercent < pulsePercent ? 1.0f : -1.0f;
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

package fr.guehenneux.audio;

/**
 * @author Jonathan Guéhenneux
 */
public class FrequencyBandLevel {

	private double level;
	private int binCount;

	/**
     * 
     */
	public FrequencyBandLevel() {

		level = 0;
		binCount = 0;
	}

	/**
	 * @param add
	 */
	public void add(double add) {

		level += add;
		binCount++;
	}

	/**
	 * @return level
	 */
	public double getLevel() {
		return level / binCount;
	}
}
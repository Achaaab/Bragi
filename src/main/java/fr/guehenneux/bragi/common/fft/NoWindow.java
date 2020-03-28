package fr.guehenneux.bragi.common.fft;

/**
 * no window
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.5
 */
public class NoWindow implements Window {

	public static final NoWindow INSTANCE = new NoWindow();

	/**
	 * use singleton
	 */
	private NoWindow() {

	}

	@Override
	public void apply(float[] samples) {

	}
}

package fr.guehenneux.audio;

/**
 * @author GUEHENNEUX
 */
public class Oscillo extends Module {

	private InputPort portEntree;

	private PresentationOscillo presentation;

	/**
	 * 
	 * @param name
	 */
	public Oscillo(String name) {

		super(name);

		portEntree = new InputPort();
		presentation = new PresentationOscillo(this);

	}

	@Override
	public void compute() {
		float[] samples = portEntree.read();
		presentation.afficher(samples);
	}

	/**
	 * @return portEntree
	 */
	public InputPort getPortEntree() {
		return portEntree;
	}

}

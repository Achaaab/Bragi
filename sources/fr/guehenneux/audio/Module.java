package fr.guehenneux.audio;

/**
 * 
 * @author Jonathan
 * 
 */
public abstract class Module {

	protected String name;

	/**
	 * 
	 * @param name
	 */
	public Module(String name) {
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 
	 */
	public abstract void compute();

}

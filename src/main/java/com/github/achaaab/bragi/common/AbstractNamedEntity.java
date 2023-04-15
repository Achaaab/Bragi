package com.github.achaaab.bragi.common;

/**
 * @author Jonathan Guéhenneux
 * @since 0.1.7
 */
public class AbstractNamedEntity implements NamedEntity {

	protected final String name;

	/**
	 * Creates an entity with the given name.
	 *
	 * @param name name of the entity to create
	 * @since 0.2.0
	 */
	public AbstractNamedEntity(String name) {
		this.name = name;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}
}

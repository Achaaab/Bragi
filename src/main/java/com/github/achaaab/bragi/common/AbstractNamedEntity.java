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
	 */
	public AbstractNamedEntity(String name) {
		this.name = name;
	}

	@Override
	public String name() {
		return name;
	}
}
package com.github.achaaab.bragi.core.module.producer;

import com.github.achaaab.bragi.scale.Note;

/**
 * TODO add javadoc about this record components
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.6
 */
public record Key(
		Note note,
		boolean sharp,
		float voltage,
		int code) {

}
package com.github.achaaab.bragi.codec.flac.frame;

/**
 * FLAC RESIDUAL_CODING_METHOD_PARTITIONED_RICE2
 * <a href="https://xiph.org/flac/format.html#partitioned_rice2">FLAC specifications</a>
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.8
 */
public class Rice2 extends Rice {

	public static final int PARAMETER_SIZE = 5;

	/**
	 * Creates a Rice method for residual coding with Rice parameters coded with {@link #PARAMETER_SIZE} bits.
	 *
	 * @since 0.2.0
	 */
	Rice2() {
		super(PARAMETER_SIZE);
	}
}

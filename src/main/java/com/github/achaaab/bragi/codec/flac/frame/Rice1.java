package com.github.achaaab.bragi.codec.flac.frame;

/**
 * FLAC RESIDUAL_CODING_METHOD_PARTITIONED_RICE
 * <a href="https://xiph.org/flac/format.html#partitioned_rice">FLAC specifications</a>
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.8
 */
public class Rice1 extends Rice {

	public static final int PARAMETER_SIZE = 4;

	/**
	 * Creates a Rice method for residual coding with Rice parameters coded with {@link #PARAMETER_SIZE} bits.
	 *
	 * @since 0.2.0
	 */
	Rice1() {
		super(PARAMETER_SIZE);
	}
}

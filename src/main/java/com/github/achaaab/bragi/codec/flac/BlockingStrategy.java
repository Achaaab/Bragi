package com.github.achaaab.bragi.codec.flac;

/**
 * FLAC blocking strategy
 *
 * <a href="https://xiph.org/flac/format.html#frame_header">FLAC specifications</a>
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.7
 */
public enum BlockingStrategy {

	FIXED_BLOCK_SIZE, VARIABLE_BLOCK_SIZE;

	private static final BlockingStrategy[] DECODING_TABLE = {
			FIXED_BLOCK_SIZE,
			VARIABLE_BLOCK_SIZE
	};

	/**
	 * @param code code of the blocking strategy, must be an unsigned 1-bit integer (in [0, 1])
	 * @return blocking strategy corresponding to the given code
	 */
	static BlockingStrategy decode(int code) {
		return DECODING_TABLE[code];
	}
}
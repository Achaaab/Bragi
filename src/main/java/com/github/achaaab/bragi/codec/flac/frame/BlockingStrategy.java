package com.github.achaaab.bragi.codec.flac.frame;

import com.github.achaaab.bragi.codec.flac.FlacException;
import com.github.achaaab.bragi.codec.flac.FlacInputStream;

import java.io.IOException;

/**
 * FLAC blocking strategy
 * <a href="https://xiph.org/flac/format.html#frame_header">FLAC specifications</a>
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.7
 */
public enum BlockingStrategy {

	FIXED_BLOCK_SIZE, VARIABLE_BLOCK_SIZE;

	private static final BlockingStrategy[] DECODING_TABLE = {
			FIXED_BLOCK_SIZE,
			VARIABLE_BLOCK_SIZE };

	/**
	 * @param input FLAC input stream from which to read a method for residual coding
	 * @return decoded blocking strategy
	 * @throws IOException I/O exception while reading from the given FLAC input stream
	 * @throws FlacException if the read code is unknown
	 * @since 0.2.0
	 */
	public static BlockingStrategy decode(FlacInputStream input) throws IOException, FlacException {

		var code = input.readUnsignedInteger(1);

		if (code >= DECODING_TABLE.length) {
			throw new FlacException("unknown code for blocking strategy: " + code);
		}

		return DECODING_TABLE[code];
	}
}

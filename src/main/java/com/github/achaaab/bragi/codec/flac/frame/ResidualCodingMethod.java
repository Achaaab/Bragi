package com.github.achaaab.bragi.codec.flac.frame;

import com.github.achaaab.bragi.codec.flac.FlacException;
import com.github.achaaab.bragi.codec.flac.FlacInputStream;

import java.io.IOException;

/**
 * FLAC method for residual coding
 * <a href="https://xiph.org/flac/format.html#residual">FLAC specifications</a>
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.8
 */
public interface ResidualCodingMethod {

	ResidualCodingMethod RICE_1 = new Rice1();
	ResidualCodingMethod RICE_2 = new Rice2();

	ResidualCodingMethod[] DECODING_TABLE = {
			RICE_1,
			RICE_2 };

	/**
	 * @param input FLAC input stream from which to read a method for residual coding
	 * @return decoded method for residual coding
	 * @throws IOException I/O exception while reading from the given FLAC input stream
	 * @throws FlacException if the read code is unknown
	 * @since 0.2.0
	 */
	static ResidualCodingMethod decode(FlacInputStream input) throws IOException, FlacException {

		var code = input.readUnsignedInteger(2);

		if (code >= DECODING_TABLE.length) {
			throw new FlacException("unknown code for residual coding method: " + code);
		}

		return DECODING_TABLE[code];
	}

	/**
	 * Decodes residuals from the given FLAC input stream into the given samples.
	 *
	 * @param input FLAC input stream from which to decode residuals
	 * @param samples array in which to store decoded residuals
	 * @param warmUpSampleCount number of warm-up samples already decoded
	 * @throws IOException I/O exception while reading from the given FLAC input stream
	 * @throws FlacException if invalid Rice partition is read
	 * @since 0.2.0
	 */
	void decode(FlacInputStream input, long[] samples, int warmUpSampleCount) throws IOException, FlacException;
}

package com.github.achaaab.bragi.codec.flac.frame;

import com.github.achaaab.bragi.codec.flac.FlacException;
import com.github.achaaab.bragi.codec.flac.FlacInputStream;

import java.io.IOException;

/**
 * FLAC SUBFRAME_LPC
 * <p>
 * <a href="https://xiph.org/flac/format.html#subframe_lpc">FLAC specifications</a>
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.8
 */
public class SubframeLpc extends SubframeLinearPredictor {

	/**
	 * Creates a FLAC LPC subframe.
	 *
	 * @param frameHeader header of the enclosing frame
	 * @param header      header of this subframe
	 * @param input       FLAC input stream from which to read the fixed subframe
	 * @param extraBit    whether to add an extra bit (used for difference channel)
	 * @throws IOException   I/O exception while reading from the given FLAC input stream
	 * @throws FlacException if invalid fixed subframe is decoded
	 */
	public SubframeLpc(FrameHeader frameHeader, SubframeHeader header, FlacInputStream input, boolean extraBit)
			throws IOException, FlacException {

		super(frameHeader, header, input, header.subframeType() - 31, extraBit);
	}

	@Override
	protected void configure() throws IOException {

		var precision = input.readUnsignedInteger(4) + 1;
		shift = input.readSignedInt(5);

		coefficients = new int[order];

		for (var coefficientIndex = 0; coefficientIndex < coefficients.length; coefficientIndex++) {
			coefficients[coefficientIndex] = input.readSignedInt(precision);
		}
	}
}
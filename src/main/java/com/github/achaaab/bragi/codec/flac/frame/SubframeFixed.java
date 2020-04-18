package com.github.achaaab.bragi.codec.flac.frame;

import com.github.achaaab.bragi.codec.flac.FlacException;
import com.github.achaaab.bragi.codec.flac.FlacInputStream;

import java.io.IOException;

/**
 * FLAC SUBFRAME_FIXED
 * <p>
 * <a href="https://xiph.org/flac/format.html#subframe_fixed">FLAC specifications</a>
 * <p>
 * Subframe encoded with fixed linear predictor.
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.8
 */
public class SubframeFixed extends SubframeLinearPredictor {

	private static final int[][] COEFFICIENTS = {
			{},
			{ 1 },
			{ 2, -1 },
			{ 3, -3, 1 },
			{ 4, -6, 4, -1 },
	};

	/**
	 * Creates a FLAC fixed subframe.
	 *
	 * @param frameHeader header of the enclosing frame
	 * @param header      header of this subframe
	 * @param input       FLAC input stream from which to read the fixed subframe
	 * @param extraBit    whether to add an extra bit (used for difference channel)
	 * @throws IOException   I/O exception while reading from the given FLAC input stream
	 * @throws FlacException if invalid fixed subframe is decoded
	 */
	public SubframeFixed(FrameHeader frameHeader, SubframeHeader header, FlacInputStream input, boolean extraBit)
			throws IOException, FlacException {

		super(frameHeader, header, input, header.subframeType() - 8, extraBit);
	}

	@Override
	protected void configure() {

		coefficients = COEFFICIENTS[order];
		shift = 0;
	}
}
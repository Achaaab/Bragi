package com.github.achaaab.bragi.codec.flac.frame;

import com.github.achaaab.bragi.codec.flac.FlacException;
import com.github.achaaab.bragi.codec.flac.FlacInputStream;

import java.io.IOException;

/**
 * FLAC SUBFRAME_HEADER
 * <a href="https://xiph.org/flac/format.html#subframe_header">FLAC specifications</a>
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.8
 */
public class SubframeHeader {

	private final int padding;
	private final int subframeType;
	private final int wastedBitCount;

	/**
	 * Creates a subframe header reading from the given FLAC input stream.
	 *
	 * @param input FLAC input stream
	 * @throws IOException   I/O exception while reading the FLAC input stream
	 * @throws FlacException if invalid or unsupported subframe header is decoded
	 * @since 0.2.0
	 */
	public SubframeHeader(FlacInputStream input) throws IOException, FlacException {

		padding = input.readUnsignedInteger(1);

		if (padding != 0) {
			throw new FlacException("0 bit padding expected");
		}

		subframeType = input.readUnsignedInteger(6);

		var wastedBitsFlag = input.readUnsignedInteger(1);
		wastedBitCount = wastedBitsFlag == 1 ? input.readUnary(0) : 0;
	}

	/**
	 * zero bit padding, to prevent sync-fooling string of 1s
	 *
	 * @return {@code 0} bit padding
	 * @since 0.2.0
	 */
	public int padding() {
		return padding;
	}

	/**
	 * @return type of the subframe
	 * @since 0.2.0
	 */
	public int subframeType() {
		return subframeType;
	}

	/**
	 * @return number of wasted bits per samples
	 * @since 0.2.0
	 */
	public int wastedBitCount() {
		return wastedBitCount;
	}
}

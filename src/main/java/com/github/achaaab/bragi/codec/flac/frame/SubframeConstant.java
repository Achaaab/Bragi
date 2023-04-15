package com.github.achaaab.bragi.codec.flac.frame;

import com.github.achaaab.bragi.codec.flac.FlacInputStream;

import java.io.IOException;

import static java.util.Arrays.fill;

/**
 * FLAC SUBFRAME_CONSTANT
 * <a href="https://xiph.org/flac/format.html#subframe_constant">FLAC specifications</a>
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.8
 */
public class SubframeConstant extends Subframe {

	/**
	 * Creates a FLAC constant subframe reading from the given FLAC input stream.
	 *
	 * @param frameHeader header of the enclosing frame
	 * @param header header of this subframe
	 * @param input FLAC input stream
	 * @param extraBit whether to add an extra bit (used for difference channel)
	 * @throws IOException I/O exception while reading the constant subframe
	 * @since 0.2.0
	 */
	public SubframeConstant(FrameHeader frameHeader, SubframeHeader header, FlacInputStream input, boolean extraBit)
			throws IOException {

		super(frameHeader, header, input, extraBit);

		var sample = input.readSignedInt(sampleSize);

		fill(samples, sample);
		insertWastedBits();
	}
}

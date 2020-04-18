package com.github.achaaab.bragi.codec.flac.frame;

import com.github.achaaab.bragi.codec.flac.FlacInputStream;

import java.io.IOException;

/**
 * FLAC SUBFRAME_VERBATIM
 * <p>
 * <a href="https://xiph.org/flac/format.html#subframe_verbatim">FLAC specifications</a>
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.8
 */
public class SubframeVerbatim extends Subframe {

	/**
	 * Creates a FLAC verbatim subframe.
	 *
	 * @param frameHeader header of the enclosing frame
	 * @param header      header of this subframe
	 * @param input       FLAC input stream from which to read the verbatim subframe
	 * @param extraBit    whether to add an extra bit (used for difference channel)
	 * @throws IOException I/O exception while reading from the given FLAC input stream
	 */
	public SubframeVerbatim(FrameHeader frameHeader, SubframeHeader header, FlacInputStream input, boolean extraBit)
			throws IOException {

		super(frameHeader, header, input, extraBit);

		for (var sampleIndex = 0; sampleIndex < sampleCount; sampleIndex++) {
			samples[sampleIndex] = input.readSignedInt(sampleSize);
		}

		insertWastedBits();
	}
}
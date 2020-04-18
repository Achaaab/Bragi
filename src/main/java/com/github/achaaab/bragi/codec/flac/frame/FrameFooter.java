package com.github.achaaab.bragi.codec.flac.frame;

import com.github.achaaab.bragi.codec.flac.FlacInputStream;

import java.io.IOException;

/**
 * FLAC FRAME_FOOTER
 * <p>
 * <a href="https://xiph.org/flac/format.html#frame_footer">FLAC specifications</a>
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.8
 */
public class FrameFooter {

	private final int crc16;

	/**
	 * Creates a FLAC frame footer reading from the given FLAC input stream.
	 *
	 * @param input FLAC input stream from which to read the frame
	 */
	public FrameFooter(FlacInputStream input) throws IOException {
		crc16 = input.readUnsignedInteger(16);
	}

	/**
	 * @return Cyclic Redundancy Check (16 bits)
	 */
	public int crc16() {
		return crc16;
	}
}
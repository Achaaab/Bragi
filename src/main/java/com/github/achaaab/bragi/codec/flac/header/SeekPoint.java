package com.github.achaaab.bragi.codec.flac.header;

import com.github.achaaab.bragi.codec.flac.FlacInputStream;

import java.io.IOException;

/**
 * FLAC SEEKPOINT
 * <a href="https://xiph.org/flac/format.html#seekpoint">FLAC specifications</a>
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.7
 */
public class SeekPoint {

	/**
	 * length of seek points in bytes
	 */
	static final int LENGTH = 18;

	private final long sampleNumber;
	private final long offset;
	private final int sampleCount;

	/**
	 * Decodes a seek point from the given FLAC input stream.
	 *
	 * @param input FLAC input stream to decode
	 * @throws IOException I/O exception while decoding a seek point
	 * @since 0.2.0
	 */
	public SeekPoint(FlacInputStream input) throws IOException {

		sampleNumber = (long) input.readUnsignedInteger(32) << 32 | input.readUnsignedInteger(32);
		offset = (long) input.readUnsignedInteger(32) << 32 | input.readUnsignedInteger(32);
		sampleCount = input.readUnsignedInteger(16);
	}

	/**
	 * @return sample number of the first sample in the target frame, or 0xFFFFFFFFFFFFFFFF for a placeholder point
	 * @since 0.2.0
	 */
	public long sampleNumber() {
		return sampleNumber;
	}

	/**
	 * @return offset in bytes from the first byte of the first frame header
	 * to the first byte of the target frame's header
	 * @since 0.2.0
	 */
	public long offset() {
		return offset;
	}

	/**
	 * @return number of samples in the target frame
	 * @since 0.2.0
	 */
	public int sampleCount() {
		return sampleCount;
	}
}

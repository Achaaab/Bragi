package com.github.achaaab.bragi.codec.flac.frame;

import com.github.achaaab.bragi.codec.flac.FlacInputStream;

/**
 * FLAC SUBFRAME
 * <p>
 * <a href="https://xiph.org/flac/format.html#subframe">FLAC specifications</a>
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.8
 */
public abstract class Subframe {

	protected final SubframeHeader header;
	protected final FlacInputStream input;

	protected final int wastedBitCount;
	protected final int sampleSize;
	protected final int sampleCount;
	protected final long[] samples;

	/**
	 * Creates an abstract FLAC subframe with its header.
	 *
	 * @param frameHeader header of the enclosing frame
	 * @param header      header of this subframe
	 * @param input       FLAC input stream from which to read this subframe
	 * @param extraBit    whether samples have an extra bit (used in difference channel)
	 */
	public Subframe(FrameHeader frameHeader, SubframeHeader header, FlacInputStream input, boolean extraBit) {

		this.header = header;
		this.input = input;

		wastedBitCount = header.wastedBitCount();
		sampleSize = frameHeader.sampleSize() - wastedBitCount + (extraBit ? 1 : 0);
		sampleCount = frameHeader.blockSize();

		samples = new long[sampleCount];
	}

	/**
	 * @return samples of this subframe
	 */
	public long[] samples() {
		return samples;
	}

	/**
	 * Shifts samples to the left, inserting zeros to the right.
	 */
	protected void insertWastedBits() {

		if (wastedBitCount > 0) {

			for (var sampleIndex = 0; sampleIndex < sampleCount; sampleIndex++) {
				samples[sampleIndex] <<= wastedBitCount;
			}
		}
	}
}
package com.github.achaaab.bragi.codec.flac.frame;

import com.github.achaaab.bragi.codec.flac.FlacException;
import com.github.achaaab.bragi.codec.flac.FlacInputStream;
import com.github.achaaab.bragi.codec.flac.header.StreamInfo;

import java.io.IOException;

/**
 * FLAC FRAME
 * <p>
 * <a href="https://xiph.org/flac/format.html#frame">FLAC specifications</a>
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.7
 */
public class Frame {

	private final FlacInputStream input;
	private final FrameHeader header;
	private final Subframe[] subframes;
	private final FrameFooter footer;

	private final int[][] samples;

	/**
	 * @param input      FLAC input stream from which to read the frame
	 * @param streamInfo FLAC global stream information
	 * @throws IOException   I/O exception while reading the frame
	 * @throws FlacException if invalid or unsupported frame is decoded
	 */
	public Frame(FlacInputStream input, StreamInfo streamInfo) throws IOException, FlacException {

		this.input = input;

		header = new FrameHeader(input, streamInfo);
		var channelAssignment = header.channelAssignment();

		var channelCount = streamInfo.channelCount();
		subframes = new Subframe[channelCount];

		for (var channelIndex = 0; channelIndex < channelCount; channelIndex++) {

			var extraBit = channelAssignment.extraBit(channelIndex);
			subframes[channelIndex] = decodeSubframe(extraBit);
		}

		samples = channelAssignment.assembleSubFrames(this);

		input.alignToByte();
		footer = new FrameFooter(input);
	}

	/**
	 * Decodes a subframe from the FLAC input stream.
	 *
	 * @return decoded subframe
	 * @throws IOException   I/O exception while reading from the FLAC input stream
	 * @throws FlacException if invalid subframe is decoded
	 */
	private Subframe decodeSubframe(boolean extraBit) throws IOException, FlacException {

		var subframeHeader = new SubframeHeader(input);
		var subframeType = subframeHeader.subframeType();

		Subframe subframe;

		if (subframeType == 0) {
			subframe = new SubframeConstant(header, subframeHeader, input, extraBit);
		} else if (subframeType == 1) {
			subframe = new SubframeVerbatim(header, subframeHeader, input, extraBit);
		} else if (subframeType >= 8 && subframeType <= 12) {
			subframe = new SubframeFixed(header, subframeHeader, input, extraBit);
		} else if (subframeType >= 32) {
			subframe = new SubframeLpc(header, subframeHeader, input, extraBit);
		} else {
			throw new FlacException("unsupported subframe type: " + subframeType);
		}

		return subframe;
	}

	/**
	 * @return header of this frame
	 */
	public FrameHeader header() {
		return header;
	}

	/**
	 * @return subframes (1 subframe per channel)
	 */
	public Subframe[] subframes() {
		return subframes;
	}

	/**
	 * @return footer of this frame
	 */
	public FrameFooter footer() {
		return footer;
	}

	/**
	 * @return decoded samples of this frame
	 */
	public int[][] samples() {
		return samples;
	}
}
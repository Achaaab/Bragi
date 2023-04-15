package com.github.achaaab.bragi.codec.flac.channel;

import com.github.achaaab.bragi.codec.flac.frame.Frame;

/**
 * 2 channels:
 * <ol start="0">
 *     <li>left</li>
 *     <li>side (difference)</li>
 * </ol>
 *
 * <a href="https://xiph.org/flac/format.html#frame_header">FLAC specifications</a>
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.7
 */
public class LeftSideStereo extends AbstractChannelAssignment {

	/**
	 * @see #LEFT_SIDE_STEREO
	 * @since 0.2.0
	 */
	LeftSideStereo() {
		super(LEFT, SIDE);
	}

	@Override
	public boolean extraBit(int channelIndex) {
		return channelIndex == 1;
	}

	@Override
	public int[][] assembleSubFrames(Frame frame) {

		var header = frame.header();
		var subframes = frame.subframes();
		var sampleCount = header.blockSize();
		var left = subframes[0].samples();
		var side = subframes[1].samples();

		var leftSamples = new int[sampleCount];
		var rightSamples = new int[sampleCount];

		for (var sampleIndex = 0; sampleIndex < sampleCount; sampleIndex++) {

			var leftSample = left[sampleIndex];
			var sideSample = side[sampleIndex];

			leftSamples[sampleIndex] = (int) leftSample;
			rightSamples[sampleIndex] = (int) (leftSample - sideSample);
		}

		return new int[][] { leftSamples, rightSamples };
	}
}

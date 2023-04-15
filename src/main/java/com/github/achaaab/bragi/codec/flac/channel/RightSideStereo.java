package com.github.achaaab.bragi.codec.flac.channel;

import com.github.achaaab.bragi.codec.flac.frame.Frame;

/**
 * 2 channels:
 * <ol start="0">
 *     <li>side (difference)</li>
 *     <li>right</li>
 * </ol>
 *
 * <a href="https://xiph.org/flac/format.html#frame_header">FLAC specifications</a>
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.7
 */
public class RightSideStereo extends AbstractChannelAssignment {

	/**
	 * @see #RIGHT_SIDE_STEREO
	 * @since 0.2.0
	 */
	RightSideStereo() {
		super(SIDE, RIGHT);
	}

	@Override
	public boolean extraBit(int channelIndex) {
		return channelIndex == 0;
	}

	@Override
	public int[][] assembleSubFrames(Frame frame) {

		var header = frame.header();
		var subframes = frame.subframes();
		var sampleCount = header.blockSize();
		var side = subframes[0].samples();
		var right = subframes[1].samples();

		var leftSamples = new int[sampleCount];
		var rightSamples = new int[sampleCount];

		for (var sampleIndex = 0; sampleIndex < sampleCount; sampleIndex++) {

			var sideSample = side[sampleIndex];
			var rightSample = right[sampleIndex];

			leftSamples[sampleIndex] = (int) (sideSample + rightSample);
			rightSamples[sampleIndex] = (int) rightSample;
		}

		return new int[][] { leftSamples, rightSamples };
	}
}

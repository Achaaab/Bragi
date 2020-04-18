package com.github.achaaab.bragi.codec.flac.channel;

import com.github.achaaab.bragi.codec.flac.frame.Frame;

/**
 * channel assignments where channels are uncorrelated
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.7
 */
public class UncorrelatedChannelAssignment extends AbstractChannelAssignment {

	/**
	 * The number of descriptions defines the number of channels.
	 *
	 * @param descriptions description of each channel
	 */
	UncorrelatedChannelAssignment(String... descriptions) {
		super(descriptions);
	}

	@Override
	public boolean extraBit(int channelIndex) {
		return false;
	}

	@Override
	public int[][] assembleSubFrames(Frame frame) {

		var header = frame.header();
		var subframes = frame.subframes();
		var sampleCount = header.blockSize();

		var samples = new int[channelCount][sampleCount];

		for (var channelIndex = 0; channelIndex < channelCount; channelIndex++) {

			var subframe = subframes[channelIndex];
			var subframeSamples = subframe.samples();

			for (var sampleIndex = 0; sampleIndex < sampleCount; sampleIndex++) {
				samples[channelIndex][sampleIndex] = (int) subframeSamples[sampleIndex];
			}
		}

		return samples;
	}
}
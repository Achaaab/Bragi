package com.github.achaaab.bragi.codec.flac.channel;

import com.github.achaaab.bragi.codec.flac.FlacDecoderException;
import com.github.achaaab.bragi.codec.flac.FlacInputStream;

import java.io.IOException;

import static com.github.achaaab.bragi.codec.flac.FlacDecoder.decodeSubframe;

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
	public long[][] decodeSubframes(FlacInputStream input, int blockSize, int sampleSize)
			throws IOException, FlacDecoderException {

		var samples = new long[channelCount][blockSize];

		for (var channelIndex = 0; channelIndex < channelCount; channelIndex++) {
			decodeSubframe(input, sampleSize, samples[channelIndex]);
		}

		return samples;
	}
}
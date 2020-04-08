package com.github.achaaab.bragi.codec.flac.channel;

import com.github.achaaab.bragi.codec.flac.FlacDecoderException;
import com.github.achaaab.bragi.codec.flac.FlacInputStream;

import java.io.IOException;

import static com.github.achaaab.bragi.codec.flac.FlacDecoder.decodeSubframe;

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
	 */
	LeftSideStereo() {
		super(LEFT, SIDE);
	}

	@Override
	public long[][] decodeSubframes(FlacInputStream input, int blockSize, int sampleSize)
			throws IOException, FlacDecoderException {

		var left = new long[sampleSize];
		var side = new long[sampleSize];

		decodeSubframe(input, sampleSize, left);
		decodeSubframe(input, sampleSize + 1, side);

		// we compute right channel samples in place, in side samples

		for (var sampleIndex = 0; sampleIndex < blockSize; sampleIndex++) {
			side[sampleIndex] = left[sampleIndex] - side[sampleIndex];
		}

		return new long[][] { left, side };
	}
}
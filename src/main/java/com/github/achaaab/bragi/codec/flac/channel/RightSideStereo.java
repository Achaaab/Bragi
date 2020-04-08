package com.github.achaaab.bragi.codec.flac.channel;

import com.github.achaaab.bragi.codec.flac.FlacDecoderException;
import com.github.achaaab.bragi.codec.flac.FlacInputStream;

import java.io.IOException;

import static com.github.achaaab.bragi.codec.flac.FlacDecoder.decodeSubframe;

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
	 */
	RightSideStereo() {
		super(SIDE, RIGHT);
	}

	@Override
	public long[][] decodeSubframes(FlacInputStream input, int blockSize, int sampleSize)
			throws IOException, FlacDecoderException {

		var side = new long[sampleSize];
		var right = new long[sampleSize];

		decodeSubframe(input, sampleSize + 1, side);
		decodeSubframe(input, sampleSize, right);

		// we compute left channel samples in place, in side samples

		for (var sampleIndex = 0; sampleIndex < blockSize; sampleIndex++) {
			side[sampleIndex] += right[sampleIndex];
		}

		return new long[][] { side, right };
	}
}
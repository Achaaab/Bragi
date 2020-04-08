package com.github.achaaab.bragi.codec.flac.channel;

import com.github.achaaab.bragi.codec.flac.FlacDecoderException;
import com.github.achaaab.bragi.codec.flac.FlacInputStream;

import java.io.IOException;

import static com.github.achaaab.bragi.codec.flac.FlacDecoder.decodeSubframe;

/**
 * 2 channels:
 * <ol start="0">
 *     <li>mid (average)</li>
 *     <li>side (difference)</li>
 * </ol>
 *
 * <a href="https://xiph.org/flac/format.html#frame_header">FLAC specifications</a>
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.7
 */
public class MidSideStereo extends AbstractChannelAssignment {

	/**
	 * @see #MID_SIDE_STEREO
	 */
	MidSideStereo() {
		super(MID, SIDE);
	}

	@Override
	public long[][] decodeSubframes(FlacInputStream input, int blockSize, int sampleSize)
			throws IOException, FlacDecoderException {

		var mid = new long[sampleSize];
		var side = new long[sampleSize];

		decodeSubframe(input, sampleSize, mid);
		decodeSubframe(input, sampleSize + 1, side);

		// we compute left channel samples in place, in mid samples
		// we compute right channel samples in place, in side samples

		for (var sampleIndex = 0; sampleIndex < blockSize; sampleIndex++) {

			var midSample = mid[sampleIndex];
			var sideSample = side[sampleIndex];

			mid[sampleIndex] = midSample + sideSample / 2;
			side[sampleIndex] = midSample - sideSample / 2;
		}

		return new long[][] { mid, side };
	}
}
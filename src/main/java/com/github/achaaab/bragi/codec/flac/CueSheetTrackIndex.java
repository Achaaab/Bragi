package com.github.achaaab.bragi.codec.flac;

import java.io.IOException;

/**
 * FLAC CUESHEET_TRACK_INDEX
 *
 * <a href="https://xiph.org/flac/format.html#cuesheet_track_index">FLAC specifications</a>
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.7
 */
public class CueSheetTrackIndex {

	private final long offset;
	private final int number;

	/**
	 * Decodes a CUE sheet track index from the given FLAC input stream.
	 *
	 * @param input FLAC input stream to decode
	 */
	public CueSheetTrackIndex(FlacInputStream input) throws IOException {

		offset = (long) input.readUnsignedInteger(32) << 32 | input.readUnsignedInteger(32);
		number = input.readUnsignedInteger(8);

		// reserved
		input.readUnsignedInteger(24);
	}

	/**
	 * For CD-DA, the offset must be evenly divisible by 588 samples
	 * (588 samples = 44100 samples/sec * 1/75th of a sec).
	 * Note that the offset is from the beginning of the track, not the beginning of the audio data.
	 *
	 * @return offset in samples, relative to the track offset
	 */
	public long offset() {
		return offset;
	}

	/**
	 * For CD-DA, an index number of 0 corresponds to the track pre-gap.
	 * The first index in a track must have a number of 0 or 1, and subsequently, index numbers must increase by 1.
	 * Index numbers must be unique within a track.
	 *
	 * @return index point number
	 */
	public int number() {
		return number;
	}
}
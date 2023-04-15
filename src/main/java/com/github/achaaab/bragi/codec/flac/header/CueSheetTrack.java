package com.github.achaaab.bragi.codec.flac.header;

import com.github.achaaab.bragi.codec.flac.FlacInputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * FLAC CUESHEET_TACK
 * <a href="https://xiph.org/flac/format.html#cuesheet_track">FLAC specifications</a>
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.7
 */
public class CueSheetTrack {

	private final long offset;
	private final int number;
	private final String isrc;
	private final boolean audio;
	private final boolean preEmphasis;
	private final List<CueSheetTrackIndex> index;

	/**
	 * Decodes a CUE sheet track from the given FLAC input stream.
	 *
	 * @param input FLAC input stream to decode
	 * @throws IOException I/O exception while decoding a CUE sheet track
	 * @since 0.2.0
	 */
	public CueSheetTrack(FlacInputStream input) throws IOException {

		offset = (long) input.readUnsignedInteger(32) << 32 | input.readUnsignedInteger(32);
		number = input.readUnsignedInteger(8);
		isrc = input.readAsciiString(12, true);
		audio = input.readUnsignedInteger(1) == 0;
		preEmphasis = input.readUnsignedInteger(1) == 1;

		// reserved
		input.alignToByte();
		input.skip(13);

		var indexCount = input.readUnsignedInteger(8);
		index = new ArrayList<>(indexCount);

		while (index.size() < indexCount) {
			index.add(new CueSheetTrackIndex(input));
		}
	}

	/**
	 * It is the offset to the first index point of the track.
	 * (Note how this differs from CD-DA, where the track's offset in the TOC is that of the track's INDEX 01
	 * even if there is an INDEX 00.)
	 * For CD-DA, the offset must be evenly divisible by 588 samples
	 * (588 samples = 44100 samples/sec * 1/75th of a sec).
	 *
	 * @return track offset in samples, relative to the beginning of the FLAC audio stream
	 * @since 0.2.0
	 */
	public long offset() {
		return offset;
	}

	/**
	 * A track number of 0 is not allowed to avoid conflicting with the CD-DA spec,
	 * which reserves this for the lead-in.
	 * For CD-DA the number must be 1-99, or 170 for the lead-out; for non-CD-DA,
	 * the track number must for 255 for the lead-out.
	 * It is not required but encouraged to start with track 1 and increase sequentially.
	 * Track numbers must be unique within a CUESHEET.
	 *
	 * @return track number
	 * @since 0.2.0
	 */
	public int number() {
		return number;
	}

	/**
	 * @return track ISRC
	 * @since 0.2.0
	 */
	public String isrc() {
		return isrc;
	}

	/**
	 * This corresponds to the CD-DA Q-channel control bit 3.
	 *
	 * @return whether this track is an audio track
	 * @since 0.2.0
	 */
	public boolean audio() {
		return audio;
	}

	/**
	 * This corresponds to the CD-DA Q-channel control bit 5.
	 *
	 * @return pre-emphasis flag
	 * @since 0.2.0
	 */
	public boolean preEmphasis() {
		return preEmphasis;
	}

	/**
	 * For all tracks except the lead-out track, one or more track index points.
	 *
	 * @return track index points
	 * @since 0.2.0
	 */
	public List<CueSheetTrackIndex> index() {
		return index;
	}
}

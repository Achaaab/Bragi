package com.github.achaaab.bragi.codec.flac;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * FLAC METADATA_BLOCK_CUESHEET
 *
 * <a href="https://xiph.org/flac/format.html#metadata_block_cuesheet">FLAC specifications</a>
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.7
 */
public class CueSheet extends MetadataBlockData {

	private final String catalogNumber;
	private final long leadInSampleCount;
	private final boolean compactDisc;
	private final List<CueSheetTrack> tracks;

	/**
	 * Decodes a CUE sheet from the given FLAC input stream.
	 *
	 * @param input FLAC input stream to decode
	 * @throws IOException I/O exception while decoding a CUE sheet
	 */
	public CueSheet(FlacInputStream input) throws IOException {

		catalogNumber = input.readAsciiString(128, true);
		leadInSampleCount = (long) input.readUnsignedInteger(32) << 32 | input.readUnsignedInteger(32);
		compactDisc = input.readUnsignedInteger(1) == 1;

		// reserved
		input.alignToByte();
		input.skip(258);

		var trackCount = input.readUnsignedInteger(8);
		tracks = new ArrayList<>(trackCount);

		while (tracks.size() < trackCount) {
			tracks.add(new CueSheetTrack(input));
		}
	}

	/**
	 * For CD-DA, this is a thirteen digit number.
	 *
	 * @return media catalog number
	 */
	public String catalogNumber() {
		return catalogNumber;
	}

	/**
	 * This field has meaning only for CD-DA cuesheets; for other uses it should be 0.
	 * For CD-DA, the lead-in is the TRACK 00 area where the table of contents is stored;
	 * more precisely, it is the number of samples from the first sample of the media
	 * to the first sample of the first index point of the first track.
	 * According to the Red Book, the lead-in must be silence and CD grabbing software does not usually store it;
	 * additionally, the lead-in must be at least two seconds but may be longer.
	 * For these reasons the lead-in length is stored here so that
	 * the absolute position of the first track can be computed.
	 * Note that the lead-in stored here is the number of samples up to the first index point of the first track,
	 * not necessarily to INDEX 01 of the first track; even the first track may have INDEX 00 data.
	 *
	 * @return number of lead-in samples
	 */
	public long leadInSampleCount() {
		return leadInSampleCount;
	}

	/**
	 * @return whether this CUE sheet corresponds to a Compact Disc
	 */
	public boolean compactDisc() {
		return compactDisc;
	}

	/**
	 * One or more tracks.
	 * A CUESHEET block is required to have a lead-out track; it is always the last track in the CUESHEET.
	 * For CD-DA, the lead-out track number must be 170 as specified by the Red Book, otherwise is must be 255.
	 *
	 * @return tracks
	 */
	public List<CueSheetTrack> tracks() {
		return tracks;
	}
}
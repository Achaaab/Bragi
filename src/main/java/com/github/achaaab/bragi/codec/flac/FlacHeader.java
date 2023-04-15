package com.github.achaaab.bragi.codec.flac;

import com.github.achaaab.bragi.codec.flac.header.MetadataBlock;
import com.github.achaaab.bragi.codec.flac.header.MetadataBlockHeader;
import com.github.achaaab.bragi.codec.flac.header.MetadataBlockType;
import com.github.achaaab.bragi.codec.flac.header.StreamInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.github.achaaab.bragi.codec.flac.header.MetadataBlock.decode;

/**
 * header of a FLAC stream
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.7
 */
public class FlacHeader {

	/**
	 * "fLaC" in US-ASCII
	 */
	private static final int FLAC_MARKER = 0x664C6143;

	private final int marker;
	private final StreamInfo streamInfo;
	private final List<MetadataBlock> metadataBlocks;

	/**
	 * Decodes a FLAC header from the given FLAC input stream.
	 *
	 * @param input FLAC input stream to decode
	 * @since 0.2.0
	 */
	public FlacHeader(FlacInputStream input) throws IOException, FlacException {

		marker = input.readUnsignedInteger(32);

		if (marker != FLAC_MARKER) {
			throw new FlacException("not a FLAC stream");
		}

		var metadataBlockHeader = new MetadataBlockHeader(input);
		var metadataBlockType = metadataBlockHeader.type();
		var last = metadataBlockHeader.last();

		if (metadataBlockType != MetadataBlockType.STREAMINFO) {

			throw new FlacException("first metadata block type (" + metadataBlockType + ")" +
					"is not " + MetadataBlockType.STREAMINFO);
		}

		streamInfo = new StreamInfo(input);

		metadataBlocks = new ArrayList<>();

		metadataBlocks.add(new MetadataBlock(metadataBlockHeader, streamInfo));

		while (!last) {

			var metadataBlock = decode(input);
			metadataBlocks.add(metadataBlock);
			last = metadataBlock.header().last();
		}
	}

	/**
	 * @return FLAC marker
	 * @see #FLAC_MARKER
	 * @since 0.2.0
	 */
	public int marker() {
		return marker;
	}

	/**
	 * @return first metadata block data which contain stream information
	 * @since 0.2.0
	 */
	public StreamInfo streamInfo() {
		return streamInfo;
	}

	/**
	 * @return metadata blocks
	 * @since 0.2.0
	 */
	public List<MetadataBlock> metadataBlocks() {
		return metadataBlocks;
	}
}

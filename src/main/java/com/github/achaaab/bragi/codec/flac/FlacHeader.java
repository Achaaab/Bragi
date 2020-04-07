package com.github.achaaab.bragi.codec.flac;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jonathan Guéhenneux
 * @since 0.1.7
 */
public class FlacHeader {

	private static final int FLAC_MARKER = 0x664C6143;

	private final int marker;
	private final StreamInfo streamInfo;
	private final List<MetadataBlock> metadataBlocks;

	/**
	 * Decodes a FLAC header from the given bit input stream.
	 *
	 * @param input bit input stream to decode
	 */
	FlacHeader(BitInputStream input) throws IOException, FlacDecoderException {

		marker = input.readUnsignedInteger(32);

		if (marker != FLAC_MARKER) {
			throw new FlacDecoderException("not a FLAC stream");
		}

		var metadataBlockHeader = new MetadataBlockHeader(input);
		var metadataBlockType = metadataBlockHeader.type();
		var last = metadataBlockHeader.last();

		if (metadataBlockType != MetadataBlockType.STREAMINFO) {
			throw new FlacDecoderException("first metadata block type (" + metadataBlockType + ") is not " + MetadataBlockType.STREAMINFO);
		}

		streamInfo = new StreamInfo(input);

		metadataBlocks = new ArrayList<>();

		while (!last) {

			var metadataBlock = new MetadataBlock(input);
			metadataBlocks.add(metadataBlock);
			last = metadataBlock.header().last();
		}
	}

	/**
	 * @return FLAC marker
	 * @see #FLAC_MARKER
	 */
	public int marker() {
		return marker;
	}

	/**
	 * @return first metadata block which contain stream information
	 */
	public StreamInfo streamInfo() {
		return streamInfo;
	}

	/**
	 * @return metadata blocks following the {@link MetadataBlockType#STREAMINFO} metadata block
	 */
	public List<MetadataBlock> metadataBlocks() {
		return metadataBlocks;
	}
}
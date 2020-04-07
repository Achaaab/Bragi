package com.github.achaaab.bragi.codec.flac;

import java.io.IOException;

/**
 * FLAC metadata block
 * https://xiph.org/flac/format.html#metadata_block
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.7
 */
public class MetadataBlock {

	private final MetadataBlockHeader header;
	private final MetadataBlockData data;

	/**
	 * Decodes a metadata block from the given bit input stream.
	 *
	 * @param input bit input stream to decode
	 * @throws IOException          I/O exception white decoding metadata block
	 * @throws FlacDecoderException if stream info metadata block is invalid
	 */
	MetadataBlock(BitInputStream input) throws IOException, FlacDecoderException {

		header = new MetadataBlockHeader(input);
		data = new MetadataBlockData(input, header.length());
	}

	/**
	 * @return header of this metadata block
	 */
	public MetadataBlockHeader header() {
		return header;
	}

	/**
	 * @return data of this metadata block
	 */
	public MetadataBlockData data() {
		return data;
	}
}
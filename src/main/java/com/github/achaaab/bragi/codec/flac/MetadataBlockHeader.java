package com.github.achaaab.bragi.codec.flac;

import java.io.IOException;

import static com.github.achaaab.bragi.codec.flac.MetadataBlockType.decode;

/**
 * FLAC METADATA_BLOCK_HEADER
 *
 * <a href="https://xiph.org/flac/format.html#metadata_block_header">FLAC specifications</a>
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.7
 */
public class MetadataBlockHeader {

	private final boolean last;
	private final MetadataBlockType type;
	private final int length;

	/**
	 * Decodes a metadata block header from the given bit input stream.
	 *
	 * @param input bit input stream to decode
	 * @throws IOException          I/O exception white decoding metadata block header
	 * @throws FlacDecoderException if stream info metadata block header is invalid
	 */
	MetadataBlockHeader(BitInputStream input) throws IOException, FlacDecoderException {

		last = input.readUnsignedInteger(1) == 1;
		var typeCode = input.readUnsignedInteger(7);
		type = decode(typeCode);
		length = input.readUnsignedInteger(24);
	}

	/**
	 * @return whether this metadata block is the last
	 */
	public boolean last() {
		return last;
	}

	/**
	 * @return type of this metadata block
	 */
	public MetadataBlockType type() {
		return type;
	}

	/**
	 * @return length of this metadata block data (in bytes)
	 */
	public int length() {
		return length;
	}
}
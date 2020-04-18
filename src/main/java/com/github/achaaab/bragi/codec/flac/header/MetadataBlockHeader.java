package com.github.achaaab.bragi.codec.flac.header;

import com.github.achaaab.bragi.codec.flac.FlacException;
import com.github.achaaab.bragi.codec.flac.FlacInputStream;

import java.io.IOException;

import static com.github.achaaab.bragi.codec.flac.header.MetadataBlockType.decode;

/**
 * FLAC METADATA_BLOCK_HEADER
 * <p>
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
	 * Decodes a metadata block header from the given FLAC input stream.
	 *
	 * @param input FLAC input stream to decode
	 * @throws IOException   I/O exception white decoding metadata block header
	 * @throws FlacException if stream info metadata block header is invalid
	 */
	public MetadataBlockHeader(FlacInputStream input) throws IOException, FlacException {

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
package com.github.achaaab.bragi.codec.flac;

import com.github.achaaab.bragi.codec.flac.BitInputStream;

import java.io.IOException;

/**
 * FLAC metadata block data
 * https://xiph.org/flac/format.html#metadata_block_data
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.7
 */
public class MetadataBlockData {

	/**
	 * Default constructor delegates all work to subclasses.
	 */
	MetadataBlockData() {

	}

	/**
	 * Decodes a metadata block data of given length from the given bit input stream.
	 *
	 * @param input  bit input stream to decode
	 * @param length length of the metadata block data to decode
	 * @throws IOException I/O exception while decoding metadata block data
	 */
	MetadataBlockData(BitInputStream input, int length) throws IOException {
		input.readBytes(length);
	}
}
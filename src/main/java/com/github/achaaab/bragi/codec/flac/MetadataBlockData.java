package com.github.achaaab.bragi.codec.flac;

import java.io.IOException;

/**
 * FLAC METADATA_BLOCK_DATA
 *
 * <a href="https://xiph.org/flac/format.html#metadata_block_data">FLAC specifications</a>
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
	 * Decodes a metadata block data of given length from the given FLAC input stream.
	 *
	 * @param input  FLAC input stream to decode
	 * @param length length of the metadata block data to decode
	 * @throws IOException I/O exception while decoding metadata block data
	 */
	MetadataBlockData(FlacInputStream input, int length) throws IOException {
		input.readBytes(length);
	}
}
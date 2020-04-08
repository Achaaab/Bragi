package com.github.achaaab.bragi.codec.flac;

import java.io.IOException;

/**
 * FLAC METADATA_BLOCK_PADDING
 *
 * <a href="https://xiph.org/flac/format.html#metadata_block_padding">FLAC specifications</a>
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.7
 */
public class Padding extends MetadataBlockData {

	/**
	 * Decodes a PADDING metadata block from the given FLAC input stream.
	 *
	 * @param input  FLAC input stream to decode
	 * @param length length of this metadata block data in bytes
	 * @throws IOException I/O exception while decoding a PADDING metadata block
	 */
	Padding(FlacInputStream input, int length) throws IOException {
		super(input, length);
	}
}
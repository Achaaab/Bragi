package com.github.achaaab.bragi.codec.flac;

import java.io.IOException;

/**
 * PADDING metadata block
 * https://xiph.org/flac/format.html#metadata_block_padding
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.7
 */
public class Padding extends MetadataBlockData {

	/**
	 * Decodes a PADDING metadata block from the given bit input stream.
	 *
	 * @param input  bit input stream to decode
	 * @param length length of this metadata block data
	 * @throws IOException I/O exception while decoding a PADDING metadata block
	 */
	Padding(BitInputStream input, int length) throws IOException {
		super(input, length);
	}
}
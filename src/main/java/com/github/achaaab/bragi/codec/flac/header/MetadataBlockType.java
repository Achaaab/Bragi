package com.github.achaaab.bragi.codec.flac.header;

import com.github.achaaab.bragi.codec.flac.FlacException;

/**
 * FLAC BLOCK_TYPE
 * <p>
 * <a href="https://xiph.org/flac/format.html#metadata_block_header">FLAC specifications</a>
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.7
 */
public enum MetadataBlockType {

	STREAMINFO, PADDING, APPLICATION, SEEKTABLE, VORBIS_COMMENT, CUESHEET, PICTURE;

	private static final MetadataBlockType[] DECODING_TABLE = {
			STREAMINFO,
			PADDING,
			APPLICATION,
			SEEKTABLE,
			VORBIS_COMMENT,
			CUESHEET,
			PICTURE
	};

	/**
	 * @param code metadata block type code
	 * @return metadata block type corresponding to the given code,
	 * @throws FlacException if the block type code is unsupported
	 */
	static MetadataBlockType decode(int code) throws FlacException {

		if (code < 0 || code >= DECODING_TABLE.length) {
			throw new FlacException("unsupported code for metadata block type: " + code);
		}

		return DECODING_TABLE[code];
	}
}
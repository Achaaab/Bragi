package com.github.achaaab.bragi.codec.flac;

import java.util.Map;

/**
 * FLAC metadata block type
 * https://xiph.org/flac/format.html#metadata_block_header
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.7
 */
public enum MetadataBlockType {

	STREAMINFO, PADDING, APPLICATION, SEEKTABLE, VORBIS_COMMENT, CUESHEET, PICTURE;

	private static final Map<Integer, MetadataBlockType> DECODING_TABLE = Map.of(
			0, STREAMINFO,
			1, PADDING,
			2, APPLICATION,
			3, SEEKTABLE,
			4, VORBIS_COMMENT,
			5, CUESHEET,
			6, PICTURE
	);

	/**
	 * @param code metadata block type code
	 * @return metadata block type corresponding to the given code,
	 * {@code null} if the block type code is valid, but unknown
	 * @throws FlacDecoderException if the block type code is invalid
	 */
	static MetadataBlockType decode(int code) throws FlacDecoderException {

		if (code < 0 || code >= 127) {
			throw new FlacDecoderException("invalid code for metadata block type: " + code);
		}

		return DECODING_TABLE.get(code);
	}
}
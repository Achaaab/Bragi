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

	/**
	 * Decodes a metadata block from the given bit input stream.
	 *
	 * @param input bit input stream to decode
	 * @return decoded metadata block
	 * @throws IOException          I/O exception white decoding metadata block
	 * @throws FlacDecoderException if stream info metadata block is invalid
	 */
	static MetadataBlock decode(BitInputStream input) throws IOException, FlacDecoderException {

		var header = new MetadataBlockHeader(input);
		var type = header.type();
		var length = header.length();

		var data = switch (type) {
			case STREAMINFO -> new StreamInfo(input);
			case PADDING -> new Padding(input, length);
			case APPLICATION -> new Application(input, length);
			case SEEKTABLE -> new SeekTable(input, length);
			case VORBIS_COMMENT -> new VorbisComment(input);
			default -> new MetadataBlockData(input, length);
		};

		return new MetadataBlock(header, data);
	}

	private final MetadataBlockHeader header;
	private final MetadataBlockData data;

	/**
	 * @param header header of this metadata block
	 * @param data   data of this metadata block
	 */
	MetadataBlock(MetadataBlockHeader header, MetadataBlockData data) {

		this.header = header;
		this.data = data;
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
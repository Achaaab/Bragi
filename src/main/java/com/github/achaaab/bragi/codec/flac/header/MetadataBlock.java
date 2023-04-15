package com.github.achaaab.bragi.codec.flac.header;

import com.github.achaaab.bragi.codec.flac.FlacException;
import com.github.achaaab.bragi.codec.flac.FlacInputStream;

import java.io.IOException;

/**
 * FLAC METADATA_BLOCK
 * <a href="https://xiph.org/flac/format.html#metadata_block">FLAC specifications</a>
 *
 * @param header header of this metadata block
 * @param data data of this metadata block
 * @author Jonathan Guéhenneux
 * @since 0.2.0
 */
public record MetadataBlock(MetadataBlockHeader header, MetadataBlockData data) {

	/**
	 * Decodes a metadata block from the given FLAC input stream.
	 *
	 * @param input FLAC input stream to decode
	 * @return decoded metadata block
	 * @throws IOException I/O exception white decoding metadata block
	 * @throws FlacException if stream info metadata block is invalid
	 */
	public static MetadataBlock decode(FlacInputStream input) throws IOException, FlacException {

		var header = new MetadataBlockHeader(input);
		var type = header.type();
		var length = header.length();

		var data = switch (type) {
			case STREAMINFO -> new StreamInfo(input);
			case PADDING -> new Padding(input, length);
			case APPLICATION -> new Application(input, length);
			case SEEKTABLE -> new SeekTable(input, length);
			case VORBIS_COMMENT -> new VorbisComment(input);
			case CUESHEET -> new CueSheet(input);
			case PICTURE -> new Picture(input);
		};

		return new MetadataBlock(header, data);
	}
}

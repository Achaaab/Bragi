package com.github.achaaab.bragi.codec.flac.header;

import com.github.achaaab.bragi.codec.flac.FlacException;
import com.github.achaaab.bragi.codec.flac.FlacInputStream;

import java.io.IOException;

/**
 * FLAC METADATA_BLOCK_PICTURE
 * <a href="https://xiph.org/flac/format.html#metadata_block_picture">FLAC specifications</a>
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.7
 */
public class Picture implements MetadataBlockData {

	private final PictureType type;
	private final String mimeType;
	private final String description;
	private final long width;
	private final long height;
	private final long colorDepth;
	private final long colorCount;
	private final byte[] data;

	/**
	 * Decodes a picture from the given FLAC input stream.
	 *
	 * @param input FLAC input stream to decode
	 * @throws IOException I/O exception while decoding a picture
	 * @throws FlacException if invalid or unsupported picture is decoded
	 * @since 0.2.0
	 */
	public Picture(FlacInputStream input) throws IOException, FlacException {

		type = PictureType.decode(input);
		mimeType = input.decodeAsciiString();
		description = input.decodeUtf8String();
		width = input.readBigEndianUnsignedInteger();
		height = input.readBigEndianUnsignedInteger();
		colorDepth = input.readBigEndianUnsignedInteger();
		colorCount = input.readBigEndianUnsignedInteger();
		var dataLength = input.readBigEndianUnsignedInteger();

		if (dataLength > Integer.MAX_VALUE) {

			throw new FlacException(
					"unsupported data length (" + dataLength + "), maximum is " + Integer.MAX_VALUE);
		}

		data = input.readBytes((int) dataLength);
	}

	/**
	 * @return picture type according to the ID3v2 APIC frame
	 * @since 0.2.0
	 */
	public PictureType type() {
		return type;
	}

	/**
	 * The MIME type may also be "-->" to signify that the data part is a URL of the picture
	 * instead of the picture data itself.
	 *
	 * @return MIME type
	 * @since 0.2.0
	 */
	public String mimeType() {
		return mimeType;
	}

	/**
	 * @return description of the picture
	 * @since 0.2.0
	 */
	public String description() {
		return description;
	}

	/**
	 * @return width of the picture in pixels
	 * @since 0.2.0
	 */
	public long width() {
		return width;
	}

	/**
	 * @return height of the picture in pixels
	 * @since 0.2.0
	 */
	public long height() {
		return height;
	}

	/**
	 * @return color depth of the picture in bits per pixel
	 * @since 0.2.0
	 */
	public long colorDepth() {
		return colorDepth;
	}

	/**
	 * Only for indexed-color picture (e.g. GIF).
	 * 0 for non-indexed-color pictures.
	 *
	 * @return number of colors used
	 * @since 0.2.0
	 */
	public long colorCount() {
		return colorCount;
	}

	/**
	 * @return binary picture data
	 * @since 0.2.0
	 */
	public byte[] data() {
		return data;
	}
}

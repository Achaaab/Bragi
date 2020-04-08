package com.github.achaaab.bragi.codec.flac;

import java.io.IOException;

/**
 * FLAC METADATA_BLOCK_PICTURE
 *
 * <a href="https://xiph.org/flac/format.html#metadata_block_picture">FLAC specifications</a>
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.7
 */
public class Picture extends MetadataBlockData {

	private final PictureType type;
	private final String mimeType;
	private final String description;
	private final long width;
	private final long height;
	private final long colorDepth;
	private final long colorCount;
	private final byte[] data;

	/**
	 * Decodes a picture from the given bit input stream.
	 *
	 * @param input bit input stream to decode
	 * @throws IOException          I/O exception while decoding a picture
	 * @throws FlacDecoderException if invalid or unsupported picture is decoded
	 */
	public Picture(BitInputStream input) throws IOException, FlacDecoderException {

		var typeCode = input.readBigEndianUnsignedInteger32();
		type = PictureType.decode(typeCode);
		mimeType = input.decodeAsciiString();
		description = input.decodeUtf8String();
		width = input.readBigEndianUnsignedInteger32();
		height = input.readBigEndianUnsignedInteger32();
		colorDepth = input.readBigEndianUnsignedInteger32();
		colorCount = input.readBigEndianUnsignedInteger32();
		var dataLength = input.readBigEndianUnsignedInteger32();

		if (dataLength > Integer.MAX_VALUE) {

			throw new FlacDecoderException(
					"unsupported data length (" + dataLength + "), maximum is " + Integer.MAX_VALUE);
		}

		data = input.readBytes((int) dataLength);
	}

	/**
	 * @return picture type according to the ID3v2 APIC frame
	 */
	public PictureType type() {
		return type;
	}

	/**
	 * The MIME type may also be "-->" to signify that the data part is a URL of the picture
	 * instead of the picture data itself.
	 *
	 * @return MIME type
	 */
	public String mimeType() {
		return mimeType;
	}

	/**
	 * @return description of the picture
	 */
	public String description() {
		return description;
	}

	/**
	 * @return width of the picture in pixels
	 */
	public long width() {
		return width;
	}

	/**
	 * @return height of the picture in pixels
	 */
	public long height() {
		return height;
	}

	/**
	 * @return color depth of the picture in bits per pixel
	 */
	public long colorDepth() {
		return colorDepth;
	}

	/**
	 * Only for indexed-color picture (e.g. GIF).
	 * 0 for non-indexed-color pictures.
	 *
	 * @return number of colors used
	 */
	public long colorCount() {
		return colorCount;
	}

	/**
	 * @return binary picture data
	 */
	public byte[] data() {
		return data;
	}
}
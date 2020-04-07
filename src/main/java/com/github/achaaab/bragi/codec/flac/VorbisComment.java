package com.github.achaaab.bragi.codec.flac;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * vorbis comment, also known as FLAC tags
 * https://xiph.org/flac/format.html#metadata_block_vorbis_comment
 * https://www.xiph.org/vorbis/doc/v-comment.html
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.7
 */
public class VorbisComment extends MetadataBlockData {

	public static final String FIELD_TITLE = "TITLE";
	public static final String FIELD_VERSION = "VERSION";
	public static final String FIELD_ALBUM = "ALBUM";
	public static final String FIELD_TRACK_NUMBER = "TRACKNUMBER";
	public static final String FIELD_ARTIST = "ARTIST";
	public static final String FIELD_PERFORMER = "PERFORMER";
	public static final String FIELD_COPYRIGHT = "COPYRIGHT";
	public static final String FIELD_LICENSE = "LICENSE";
	public static final String FIELD_ORGANIZATION = "ORGANIZATION";
	public static final String FIELD_DESCRIPTION = "DESCRIPTION";
	public static final String FIELD_GENRE = "GENRE";
	public static final String FIELD_DATE = "DATE";
	public static final String FIELD_LOCATION = "LOCATION";
	public static final String FIELD_CONTACT = "CONTACT";
	public static final String FIELD_ISRC = "ISRC";

	/**
	 * Reads an unsigned, little-endian, 32-bits integer from the given input and then decode an UTF-8 string of
	 * found length from the same input.
	 *
	 * @param input bit input stream to decode
	 * @return read string
	 * @throws IOException          I/O exception while reading a vorbis string
	 * @throws FlacDecoderException if string length is not supported
	 */
	private static String decodeString(BitInputStream input) throws FlacDecoderException, IOException {

		var length = input.readLittleEndianUnsignedInteger32();

		if (length > Integer.MAX_VALUE) {

			throw new FlacDecoderException(
					"unsupported string length (" + length + "), maximum is " + Integer.MAX_VALUE);
		}

		return input.readUTF((int) length);
	}

	private final String vendor;

	private final Map<String, String> userComments;

	/**
	 * Decodes a vorbis comment from the given input stream.
	 *
	 * @param input bit input stream to decode
	 * @throws IOException          I/O exception while decoding a vorbis comment
	 * @throws FlacDecoderException if invalid or unsupported vorbis comment is decoded
	 */
	public VorbisComment(BitInputStream input) throws IOException, FlacDecoderException {

		vendor = decodeString(input);

		var userCommentCount = input.readLittleEndianUnsignedInteger32();

		if (userCommentCount > Integer.MAX_VALUE) {

			throw new FlacDecoderException(
					"unsupported user comment count (" + userCommentCount + "), maximum is " + Integer.MAX_VALUE);
		}

		userComments = new HashMap<>((int) userCommentCount);

		while (userComments.size() < userCommentCount) {
			decodeUserComment(input);
		}
	}

	/**
	 * @return vorbis user comments
	 */
	public Map<String, String> userComments() {
		return userComments;
	}

	/**
	 * Decodes a vorbis user comment from the given bit input stream.
	 *
	 * @param input bit input stream to decode
	 * @throws IOException          I/O exception while decoding a user comment
	 * @throws FlacDecoderException if user comment is invalid or unsupported
	 */
	private void decodeUserComment(BitInputStream input) throws IOException, FlacDecoderException {

		var userComment = decodeString(input);
		var equalsIndex = userComment.indexOf('=');
		var fieldName = userComment.substring(0, equalsIndex);
		var fieldValue = userComment.substring(equalsIndex + 1);

		userComments.put(fieldName.toUpperCase(), fieldValue);
	}
}
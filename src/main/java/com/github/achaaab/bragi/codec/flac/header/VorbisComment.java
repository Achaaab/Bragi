package com.github.achaaab.bragi.codec.flac.header;

import com.github.achaaab.bragi.codec.flac.FlacDecoderException;
import com.github.achaaab.bragi.codec.flac.FlacInputStream;
import com.github.achaaab.bragi.codec.flac.header.MetadataBlockData;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * FLAC METADATA_BLOCK_VORBIS_COMMENT
 * It is also known as FLAC tags.
 *
 * <a href="https://xiph.org/flac/format.html#metadata_block_vorbis_comment">FLAC specifications</a>
 * <a href="https://www.xiph.org/vorbis/doc/v-comment.html">Vorbis specifications</a>
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



	private final String vendor;
	private final Map<String, String> userComments;

	/**
	 * Decodes a vorbis comment from the given input stream.
	 *
	 * @param input FLAC input stream to decode
	 * @throws IOException          I/O exception while decoding a vorbis comment
	 * @throws FlacDecoderException if invalid or unsupported vorbis comment is decoded
	 */
	public VorbisComment(FlacInputStream input) throws IOException, FlacDecoderException {

		vendor = input.decodeVorbisString();

		var userCommentCount = input.readLittleEndianUnsignedInteger();

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
	 * @return vendor name
	 */
	public String vendor() {
		return vendor;
	}

	/**
	 * @return vorbis user comments
	 */
	public Map<String, String> userComments() {
		return userComments;
	}

	/**
	 * Decodes a vorbis user comment from the given FLAC input stream.
	 *
	 * @param input FLAC input stream to decode
	 * @throws IOException          I/O exception while decoding a user comment
	 * @throws FlacDecoderException if user comment is invalid or unsupported
	 */
	private void decodeUserComment(FlacInputStream input) throws IOException, FlacDecoderException {

		var userComment = input.decodeVorbisString();
		var equalsIndex = userComment.indexOf('=');
		var fieldName = userComment.substring(0, equalsIndex);
		var fieldValue = userComment.substring(equalsIndex + 1);

		userComments.put(fieldName.toUpperCase(), fieldValue);
	}
}
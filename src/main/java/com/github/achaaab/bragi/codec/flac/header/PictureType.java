package com.github.achaaab.bragi.codec.flac.header;

import com.github.achaaab.bragi.codec.flac.FlacInputStream;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * FLAC METADATA_BLOCK_PICTURE_TYPE
 * <a href="https://xiph.org/flac/format.html#metadata_block_picture">FLAC specifications</a>
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.7
 */
public enum PictureType {

	OTHER, FILE_ICON_32_32, OTHER_FILE_ICON, COVER_FRONT, COVER_BACK, LEAFLET_PAGE, MEDIA,
	LEAD_ARTIST_LEAD_PERFORMER_SOLOIST, ARTIST_PERFORMER, CONDUCTOR, BAND_ORCHESTRA, COMPOSER, LYRICIST_TEXT_WRITER,
	RECORDING_LOCATION, DURING_RECORDING, DURING_PERFORMANCE, MOVIE_VIDEO_SCREEN_CAPTURE, BRIGHT_COLOURED_FISH,
	ILLUSTRATION, BAND_ARTIST_LOGOTYPE, PUBLISHER_STUDIO_LOGOTYPE;

	private static final Map<Long, PictureType> DECODING_TABLE;

	static {

		DECODING_TABLE = new HashMap<>(21);

		DECODING_TABLE.put(0L, OTHER);
		DECODING_TABLE.put(1L, FILE_ICON_32_32);
		DECODING_TABLE.put(2L, OTHER_FILE_ICON);
		DECODING_TABLE.put(3L, COVER_FRONT);
		DECODING_TABLE.put(4L, COVER_BACK);
		DECODING_TABLE.put(5L, LEAFLET_PAGE);
		DECODING_TABLE.put(6L, MEDIA);
		DECODING_TABLE.put(7L, LEAD_ARTIST_LEAD_PERFORMER_SOLOIST);
		DECODING_TABLE.put(8L, ARTIST_PERFORMER);
		DECODING_TABLE.put(9L, CONDUCTOR);
		DECODING_TABLE.put(10L, BAND_ORCHESTRA);
		DECODING_TABLE.put(11L, COMPOSER);
		DECODING_TABLE.put(12L, LYRICIST_TEXT_WRITER);
		DECODING_TABLE.put(13L, RECORDING_LOCATION);
		DECODING_TABLE.put(14L, DURING_RECORDING);
		DECODING_TABLE.put(15L, DURING_PERFORMANCE);
		DECODING_TABLE.put(16L, MOVIE_VIDEO_SCREEN_CAPTURE);
		DECODING_TABLE.put(17L, BRIGHT_COLOURED_FISH);
		DECODING_TABLE.put(18L, ILLUSTRATION);
		DECODING_TABLE.put(19L, BAND_ARTIST_LOGOTYPE);
		DECODING_TABLE.put(20L, PUBLISHER_STUDIO_LOGOTYPE);
	}

	/**
	 * Reads a 32-bits integer from the given FLAC input stream and returns the corresponding picture type.
	 *
	 * @param input FLAC input stream from which to decode a picture type
	 * @return decoded picture type
	 * @throws IOException I/O exception while reading from the given FLAC input stream
	 * @since 0.2.0
	 */
	static PictureType decode(FlacInputStream input) throws IOException {

		var code = input.readBigEndianUnsignedInteger();
		return DECODING_TABLE.get(code);
	}
}

package com.github.achaaab.bragi.codec.flac.header;

import com.github.achaaab.bragi.codec.flac.FlacException;
import com.github.achaaab.bragi.codec.flac.FlacInputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * FLAC METADATA_BLOCK_SEEKTABLE
 * <a href="https://xiph.org/flac/format.html#metadata_block_seektable">FLAC specifications</a>
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.7
 */
public class SeekTable implements MetadataBlockData {

	private final List<SeekPoint> seekPoints;

	/**
	 * Decodes an SEEKTABLE metadata block from the given FLAC input stream.
	 *
	 * @param input FLAC input stream to decode
	 * @param length length of this metadata block data
	 * @throws IOException I/O exception while decoding a SEEKTABLE metadata block
	 * @since 0.2.0
	 */
	SeekTable(FlacInputStream input, int length) throws IOException, FlacException {

		if (length % SeekPoint.LENGTH != 0) {

			throw new FlacException("Length of the seek table (" + length + ") " +
					"is not a multiple of seek point length (" + SeekPoint.LENGTH + ").");
		}

		var seekPointCount = length / SeekPoint.LENGTH;
		seekPoints = new ArrayList<>(seekPointCount);

		for (var seekPointIndex = 0; seekPointIndex < seekPointCount; seekPointIndex++) {
			seekPoints.add(new SeekPoint(input));
		}
	}

	/**
	 * @return seek points
	 * @since 0.2.0
	 */
	public List<SeekPoint> seekPoints() {
		return seekPoints;
	}
}

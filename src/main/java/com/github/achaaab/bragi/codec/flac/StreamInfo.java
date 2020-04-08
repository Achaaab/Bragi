package com.github.achaaab.bragi.codec.flac;

import java.io.IOException;

/**
 * FLAC METADATA_BLOCK_STREAMINFO
 * It must be the first metadata block data of a FLAC stream.
 *
 * <a href="https://xiph.org/flac/format.html#metadata_block_streaminfo">FLAC specifications</a>
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.7
 */
public class StreamInfo extends MetadataBlockData {

	private final int minimumBlockSize;
	private final int maximumBlockSize;
	private final int minimumFrameSize;
	private final int maximumFrameSize;
	private final int sampleRate;
	private final int channelCount;
	private final int sampleSize;
	private final long sampleCount;
	private final byte[] md5Signature;

	private final float duration;

	/**
	 * Decodes a STREAMINFO metadata block from the given FLAC input stream.
	 *
	 * @param input FLAC input stream to decode
	 * @throws IOException          I/O exception while decoding a STREAMINFO metadata block
	 * @throws FlacDecoderException if STREAMINFO metadata block is invalid or not supported
	 */
	StreamInfo(FlacInputStream input) throws IOException, FlacDecoderException {

		minimumBlockSize = input.readUnsignedInteger(16);
		maximumBlockSize = input.readUnsignedInteger(16);
		minimumFrameSize = input.readUnsignedInteger(24);
		maximumFrameSize = input.readUnsignedInteger(24);
		sampleRate = input.readUnsignedInteger(20);
		channelCount = input.readUnsignedInteger(3) + 1;
		sampleSize = input.readUnsignedInteger(5) + 1;
		sampleCount = (long) input.readUnsignedInteger(18) << 18 | input.readUnsignedInteger(18);
		md5Signature = input.readBytes(16);

		if (sampleSize % 8 != 0) {
			throw new FlacDecoderException("sample size not supported: " + sampleSize);
		}

		duration = (float) sampleCount / sampleRate;
	}

	/**
	 * @return minimum block size in samples
	 */
	public int minimumBlockSize() {
		return minimumBlockSize;
	}

	/**
	 * @return maximum block size in samples
	 */
	public int maximumBlockSize() {
		return maximumBlockSize;
	}

	/**
	 * @return minimum frame size in bytes (B)
	 */
	public int minimumFrameSize() {
		return minimumFrameSize;
	}

	/**
	 * @return maximum frame size in bytes (B)
	 */
	public int maximumFrameSize() {
		return maximumFrameSize;
	}

	/**
	 * @return number of samples per second per channel
	 */
	public int sampleRate() {
		return sampleRate;
	}

	/**
	 * @return number of channels
	 */
	public int channelCount() {
		return channelCount;
	}

	/**
	 * @return size of each sample in bits
	 */
	public int sampleSize() {
		return sampleSize;
	}

	/**
	 * @return number of inter-channel sample
	 */
	public long sampleCount() {
		return sampleCount;
	}

	/**
	 * @return MD5 signature
	 */
	public byte[] md5Signature() {
		return md5Signature;
	}

	/**
	 * @return stream duration in seconds (s)
	 */
	public float duration() {
		return duration;
	}
}
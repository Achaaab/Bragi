package com.github.achaaab.bragi.codec.flac.frame;

import com.github.achaaab.bragi.codec.flac.FlacException;
import com.github.achaaab.bragi.codec.flac.FlacInputStream;
import com.github.achaaab.bragi.codec.flac.channel.ChannelAssignment;
import com.github.achaaab.bragi.codec.flac.header.StreamInfo;

import java.io.IOException;

import static java.lang.Integer.numberOfLeadingZeros;

/**
 * FLAC FRAME_HEADER
 * <a href="https://xiph.org/flac/format.html#frame_header">FLAC specifications</a>
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.7
 */
public class FrameHeader {

	private static final int EXPECTED_SYNC_CODE = 0b11111111111110;

	/**
	 * Decodes a block size from the given code and if necessary from the given FLAC input stream.
	 *
	 * @param code code of the block size
	 * @param input FLAC input stream to decode
	 * @return block size in inter-channel samples
	 * @throws IOException I/O exception while decoding the block size
	 * @throws FlacException if invalid code is provided
	 * @since 0.2.0
	 */
	private static int decodeBlockSize(int code, FlacInputStream input)
			throws IOException, FlacException {

		return switch (code) {

			case 0b0001 -> 192;
			case 0b0010 -> 576;
			case 0b0011 -> 1152;
			case 0b0100 -> 2304;
			case 0b0101 -> 4608;
			case 0b0110 -> input.readUnsignedInteger(8);
			case 0b0111 -> input.readUnsignedInteger(16);
			case 0b1000 -> 256;
			case 0b1001 -> 512;
			case 0b1010 -> 1024;
			case 0b1011 -> 2048;
			case 0b1100 -> 4096;
			case 0b1101 -> 8192;
			case 0b1110 -> 16384;
			case 0b1111 -> 32768;
			default -> throw new FlacException("invalid block size code: " + code);
		};
	}

	/**
	 * Decodes a sample rate from the given code and if necessary from the stream info metadata or the FLAC
	 * input stream.
	 *
	 * @param code code of the sample rate
	 * @param streamInfo stream info metadata
	 * @param input FLAC input stream to decode
	 * @return decoded sample rate in inter-channel sample per second
	 * @throws IOException I/O exception while decoding the sample rate
	 * @throws FlacException if invalid sample rate code is provided
	 * @since 0.2.0
	 */
	private static int decodeSampleRate(int code, StreamInfo streamInfo, FlacInputStream input)
			throws IOException, FlacException {

		return switch (code) {

			case 0b0000 -> streamInfo.sampleRate();
			case 0b0001 -> 88200;
			case 0b0010 -> 176400;
			case 0b0011 -> 192000;
			case 0b0100 -> 8000;
			case 0b0101 -> 16000;
			case 0b0110 -> 22050;
			case 0b0111 -> 24000;
			case 0b1000 -> 32000;
			case 0b1001 -> 44100;
			case 0b1010 -> 48000;
			case 0b1011 -> 96000;
			case 0b1100 -> 1000 * input.readUnsignedInteger(8);
			case 0b1101 -> input.readUnsignedInteger(16);
			case 0b1110 -> 10 * input.readUnsignedInteger(16);
			default -> throw new FlacException("invalid sample rate code: " + code);
		};
	}

	/**
	 * Decodes a sample size from the given code and if necessary from the stream info metadata.
	 *
	 * @param code code of the sample size
	 * @param streamInfo stream info metadata
	 * @return decoded sample rate
	 * @throws FlacException if invalid sample size code is provided
	 * @since 0.2.0
	 */
	private static int decodeSampleSize(int code, StreamInfo streamInfo) throws FlacException {

		return switch (code) {

			case 0b000 -> streamInfo.sampleSize();
			case 0b001 -> 8;
			case 0b010 -> 12;
			case 0b100 -> 16;
			case 0b101 -> 20;
			case 0b110 -> 24;
			default -> throw new FlacException("invalid sample size code: " + code);
		};
	}

	private final int syncCode;
	private final BlockingStrategy blockingStrategy;
	private final int blockSize;
	private final int sampleRate;
	private final ChannelAssignment channelAssignment;
	private final int sampleSize;
	private final int crc8;

	/**
	 * Decodes a frame header from the given FLAC input stream.
	 *
	 * @param input FLAC input stream
	 * @param streamInfo global stream information
	 * @throws IOException I/O exception while decoding the frame header
	 * @throws FlacException if invalid or unsupported frame header is decoded
	 * @since 0.2.0
	 */
	public FrameHeader(FlacInputStream input, StreamInfo streamInfo) throws IOException, FlacException {

		syncCode = input.readUnsignedInteger(14);

		if (syncCode != EXPECTED_SYNC_CODE) {
			throw new FlacException("expecting a sync code (" + EXPECTED_SYNC_CODE + "), read: " + syncCode);
		}

		var reserved = input.readUnsignedInteger(1);

		if (reserved != 0) {
			throw new FlacException("reserved bit not supported: " + reserved);
		}

		blockingStrategy = BlockingStrategy.decode(input);

		var blockSizeCode = input.readUnsignedInteger(4);
		var sampleRateCode = input.readUnsignedInteger(4);

		channelAssignment = ChannelAssignment.decode(input);

		var sampleSizeCode = input.readUnsignedInteger(3);
		sampleSize = decodeSampleSize(sampleSizeCode, streamInfo);

		reserved = input.readUnsignedInteger(1);

		if (reserved != 0) {
			throw new FlacException("reserved bit not supported: " + reserved);
		}

		// sample number or frame number
		// TODO understand that strange "UTF-8" or "UCS-2" coding
		var octet = input.readUnsignedInteger(8);
		var length = numberOfLeadingZeros(~(octet << 24)) - 1;
		for (var index = 0; index < length; index++) {
			input.readUnsignedInteger(8);
		}

		blockSize = decodeBlockSize(blockSizeCode, input);
		sampleRate = decodeSampleRate(sampleRateCode, streamInfo, input);

		crc8 = input.readUnsignedInteger(8);
	}

	/**
	 * @return read sync code, must be equals to expected sync code
	 * @see #EXPECTED_SYNC_CODE
	 * @since 0.2.0
	 */
	public int syncCode() {
		return syncCode;
	}

	/**
	 * @return blocking strategy
	 * @since 0.2.0
	 */
	public BlockingStrategy blockingStrategy() {
		return blockingStrategy;
	}

	/**
	 * @return block size in inter-channel samples
	 * @since 0.2.0
	 */
	public int blockSize() {
		return blockSize;
	}

	/**
	 * @return sample rate in inter-channel samples per second
	 * @since 0.2.0
	 */
	public int sampleRate() {
		return sampleRate;
	}

	/**
	 * @return channel assignment
	 * @since 0.2.0
	 */
	public ChannelAssignment channelAssignment() {
		return channelAssignment;
	}

	/**
	 * @return sample size in bits
	 * @since 0.2.0
	 */
	public int sampleSize() {
		return sampleSize;
	}

	/**
	 * @return Cyclic Redundancy Check (8 bits)
	 * @since 0.2.0
	 */
	public int crc8() {
		return crc8;
	}
}

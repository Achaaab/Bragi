package com.github.achaaab.bragi.codec.flac.channel;

import com.github.achaaab.bragi.codec.flac.FlacException;
import com.github.achaaab.bragi.codec.flac.FlacInputStream;
import com.github.achaaab.bragi.codec.flac.frame.Frame;

import java.io.IOException;

/**
 * FLAC channel assignment
 * Where defined, the channel order follows SMPTE/ITU-R recommendations.
 * <p>
 * <a href="https://xiph.org/flac/format.html#frame_header">FLAC specifications</a>
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.7
 */
public interface ChannelAssignment {

	String MONO = "mono";
	String LEFT = "left";
	String RIGHT = "right";
	String CENTER = "center";
	String FRONT_LEFT = "front left";
	String FRONT_RIGHT = "front right";
	String BACK_LEFT = "back left";
	String BACK_RIGHT = "back right";
	String FRONT_CENTER = "front center";
	String LFE = "LFE";
	String BACK_SURROUND_LEFT = "back/surround left";
	String BACK_SURROUND_RIGHT = "back/surround right";
	String BACK_CENTER = "back center";
	String SIDE_LEFT = "side left";
	String SIDE_RIGHT = "side right";
	String MID = "mid (average)";
	String SIDE = "side (difference)";

	ChannelAssignment ONE_CHANNEL = new OneChannel();
	ChannelAssignment TWO_CHANNELS = new TwoChannels();
	ChannelAssignment THREE_CHANNELS = new ThreeChannels();
	ChannelAssignment FOUR_CHANNELS = new FourChannels();
	ChannelAssignment FIVE_CHANNELS = new FiveChannels();
	ChannelAssignment SIX_CHANNELS = new SixChannels();
	ChannelAssignment SEVEN_CHANNELS = new SevenChannels();
	ChannelAssignment EIGHT_CHANNELS = new EightChannels();
	ChannelAssignment LEFT_SIDE_STEREO = new LeftSideStereo();
	ChannelAssignment RIGHT_SIDE_STEREO = new RightSideStereo();
	ChannelAssignment MID_SIDE_STEREO = new MidSideStereo();

	ChannelAssignment[] DECODING_TABLE = {
			ONE_CHANNEL,
			TWO_CHANNELS,
			THREE_CHANNELS,
			FOUR_CHANNELS,
			FIVE_CHANNELS,
			SIX_CHANNELS,
			SEVEN_CHANNELS,
			EIGHT_CHANNELS,
			LEFT_SIDE_STEREO,
			RIGHT_SIDE_STEREO,
			MID_SIDE_STEREO
	};

	/**
	 * Reads a channel assignment code and returns the corresponding channel assignment.
	 *
	 * @param input FLAC input stream from which to decode a channel assignment
	 * @return decoded channel assignment
	 * @throws IOException          I/O exception while reading from the given FLAC input stream
	 * @throws FlacException if the read code is unknown
	 */
	static ChannelAssignment decode(FlacInputStream input) throws IOException, FlacException {

		var code = input.readUnsignedInteger(4);

		if (code >= DECODING_TABLE.length) {
			throw new FlacException("unknown code for channel assignment: " + code);
		}

		return DECODING_TABLE[code];
	}

	/**
	 * @return number of channels
	 */
	int channelCount();

	/**
	 * @param channelIndex index of a channel
	 * @return description of the specified channel
	 */
	String description(int channelIndex);

	/**
	 * @param channelIndex index of a channel
	 * @return whether samples of the specified channel have an extra bit (difference channel)
	 */
	boolean extraBit(int channelIndex);

	/**
	 * @param frame decoded FLAC frame
	 * @return assembled samples of the frame
	 */
	int[][] assembleSubFrames(Frame frame);
}
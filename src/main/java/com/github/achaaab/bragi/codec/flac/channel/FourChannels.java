package com.github.achaaab.bragi.codec.flac.channel;

/**
 * 4 channels:
 * <ol start="0">
 *     <li>front left</li>
 *     <li>front right</li>
 *     <li>back left</li>
 *     <li>back right</li>
 * </ol>
 *
 * <a href="https://xiph.org/flac/format.html#frame_header">FLAC specifications</a>
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.7
 */
public class FourChannels extends UncorrelatedChannelAssignment {

	/**
	 * @see #FOUR_CHANNELS
	 * @since 0.2.0
	 */
	FourChannels() {
		super(FRONT_LEFT, FRONT_RIGHT, BACK_LEFT, BACK_RIGHT);
	}
}

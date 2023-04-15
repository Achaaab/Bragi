package com.github.achaaab.bragi.codec.flac.channel;

/**
 * 5 channels:
 * <ol start="0">
 *     <li>front left</li>
 *     <li>front right</li>
 *     <li>front center</li>
 *     <li>back/surround left</li>
 *     <li>back/surround right</li>
 * </ol>
 *
 * <a href="https://xiph.org/flac/format.html#frame_header">FLAC specifications</a>
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.7
 */
public class FiveChannels extends UncorrelatedChannelAssignment {

	/**
	 * @see #FIVE_CHANNELS
	 * @since 0.2.0
	 */
	FiveChannels() {
		super(FRONT_LEFT, FRONT_RIGHT, FRONT_CENTER, BACK_SURROUND_LEFT, BACK_SURROUND_RIGHT);
	}
}

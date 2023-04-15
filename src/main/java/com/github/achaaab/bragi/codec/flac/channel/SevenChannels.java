package com.github.achaaab.bragi.codec.flac.channel;

/**
 * 7 channels:
 * <ol start="0">
 *     <li>front left</li>
 *     <li>front right</li>
 *     <li>front center</li>
 *     <li>LFE</li>
 *     <li>back center</li>
 *     <li>side left</li>
 *     <li>side right</li>
 * </ol>
 *
 * <a href="https://xiph.org/flac/format.html#frame_header">FLAC specifications</a>
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.7
 */
public class SevenChannels extends UncorrelatedChannelAssignment {

	/**
	 * @see #SEVEN_CHANNELS
	 * @since 0.2.0
	 */
	SevenChannels() {
		super(FRONT_LEFT, FRONT_RIGHT, FRONT_CENTER, LFE, BACK_CENTER, SIDE_LEFT, SIDE_RIGHT);
	}
}

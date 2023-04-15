package com.github.achaaab.bragi.codec.flac.channel;

/**
 * 2 channels:
 * <ol start="0">
 *     <li>left</li>
 *     <li>right</li>
 * </ol>
 *
 * <a href="https://xiph.org/flac/format.html#frame_header">FLAC specifications</a>
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.7
 */
public class TwoChannels extends UncorrelatedChannelAssignment {

	/**
	 * @see #TWO_CHANNELS
	 * @since 0.2.0
	 */
	TwoChannels() {
		super(LEFT, RIGHT);
	}
}

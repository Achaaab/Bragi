package com.github.achaaab.bragi.codec.flac.channel;

/**
 * 1 channel:
 * <ol start="0">
 *     <li>mono</li>
 * </ol>
 *
 * <a href="https://xiph.org/flac/format.html#frame_header">FLAC specifications</a>
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.7
 */
public class OneChannel extends UncorrelatedChannelAssignment {

	/**
	 * @see #ONE_CHANNEL
	 * @since 0.2.0
	 */
	OneChannel() {
		super(MONO);
	}
}
package com.github.achaaab.bragi.codec.flac.channel;

/**
 * 3 channels:
 * <ol start="0">
 *     <li>left</li>
 *     <li>right</li>
 *     <li>center</li>
 * </ol>
 *
 * <a href="https://xiph.org/flac/format.html#frame_header">FLAC specifications</a>
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.7
 */
public class ThreeChannels extends UncorrelatedChannelAssignment {

	/**
	 * @see #THREE_CHANNELS
	 */
	ThreeChannels() {
		super(LEFT, RIGHT, CENTER);
	}
}
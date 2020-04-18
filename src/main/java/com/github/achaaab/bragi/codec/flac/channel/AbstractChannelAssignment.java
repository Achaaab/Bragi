package com.github.achaaab.bragi.codec.flac.channel;

/**
 * abstract FLAC channel assignment
 * <p>
 * <a href="https://xiph.org/flac/format.html#frame_header">FLAC specifications</a>
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.7
 */
public abstract class AbstractChannelAssignment implements ChannelAssignment {

	protected final String[] descriptions;
	protected final int channelCount;

	/**
	 * The number of descriptions defines the number of channels.
	 *
	 * @param descriptions description of each channel
	 */
	AbstractChannelAssignment(String... descriptions) {

		this.descriptions = descriptions;

		channelCount = descriptions.length;
	}

	@Override
	public int channelCount() {
		return channelCount;
	}

	@Override
	public String description(int channelIndex) {
		return descriptions[channelIndex];
	}
}
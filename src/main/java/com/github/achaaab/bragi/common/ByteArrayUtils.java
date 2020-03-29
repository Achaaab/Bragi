package com.github.achaaab.bragi.common;

/**
 * @author Jonathan Guéhenneux
 */
public class ByteArrayUtils {

	/**
	 * @param data
	 * @param offset
	 * @param length
	 * @return
	 */
	public static String getString(final byte[] data, final int offset, final int length) {

		StringBuilder buffer = new StringBuilder();

		for (int index = offset; index < offset + length; index++) {
			buffer.append((char) data[index]);
		}

		return buffer.toString();
	}

	/**
	 * @param data
	 * @param offset
	 * @param bigEndian
	 * @return
	 */
	public static int getInteger(final byte[] data, final int offset, final boolean bigEndian) {

		byte b0 = data[offset + 0];
		byte b1 = data[offset + 1];
		byte b2 = data[offset + 2];
		byte b3 = data[offset + 3];

		int i0, i1, i2, i3;

		if (bigEndian) {

			i0 = (b0 & 0xFF) << 24;
			i1 = (b1 & 0xFF) << 16;
			i2 = (b2 & 0xFF) << 8;
			i3 = (b3 & 0xFF) << 0;

		} else {

			i0 = (b0 & 0xFF) << 0;
			i1 = (b1 & 0xFF) << 8;
			i2 = (b2 & 0xFF) << 16;
			i3 = (b3 & 0xFF) << 24;
		}

		return i0 | i1 | i2 | i3;
	}

	/**
	 * @param data
	 * @param offset
	 * @param bigEndian
	 * @return
	 */
	public static short getShort(final byte[] data, final int offset, final boolean bigEndian) {

		byte b0 = data[offset + 0];
		byte b1 = data[offset + 1];

		short s0, s1;

		if (bigEndian) {

			s0 = (short) (b0 << 8);
			s1 = (short) (b1 << 0);

		} else {

			s0 = (short) (b0 << 0);
			s1 = (short) (b1 << 8);
		}

		return (short) (s0 | s1);
	}
}
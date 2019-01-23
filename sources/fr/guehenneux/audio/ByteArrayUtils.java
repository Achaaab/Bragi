

package fr.guehenneux.audio;

/**
 * @author GUEHENNEUX
 */
public class ByteArrayUtils {

    /**
     * @param datas
     * @param offset
     * @param length
     * @return
     */
    public static final String getString(final byte[] datas, final int offset, final int length) {

        StringBuffer buffer = new StringBuffer();

        for (int index = offset; index < offset + length; index++) {
            buffer.append((char) datas[index]);
        }

        return buffer.toString();

    }

    /**
     * @param datas
     * @param offset
     * @param bigEndian
     * @return
     */
    public static final int getInteger(final byte[] datas, final int offset, final boolean bigEndian) {

        byte b0 = datas[offset + 0];
        byte b1 = datas[offset + 1];
        byte b2 = datas[offset + 2];
        byte b3 = datas[offset + 3];

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
     * @param datas
     * @param offset
     * @param bigEndian
     * @return
     */
    public static final short getShort(final byte[] datas, final int offset, final boolean bigEndian) {

        byte b0 = datas[offset + 0];
        byte b1 = datas[offset + 1];

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

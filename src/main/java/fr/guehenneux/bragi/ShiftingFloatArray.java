package fr.guehenneux.bragi;

import static java.lang.Math.max;
import static java.lang.System.arraycopy;

/**
 * @author Jonathan Guéhenneux
 */
public class ShiftingFloatArray {

	private float[] array;
	private int length;
	private int index;

	/**
	 * @param length array length
	 */
	public ShiftingFloatArray(int length) {

		this.length = length;

		array = new float[length];
		index = 0;
	}

	/**
	 * @return array length
	 */
	public int getLength() {
		return length;
	}

	/**
	 * @param index index between 0 and length - 1
	 * @return {@code float} value at given writeIndex
	 */
	public float read(int index) {
		return array[(this.index + index) % length];
	}

	/**
	 * If {@code value.length > length}, only last values are written.
	 *
	 * @param source {@code float} values to write
	 */
	public void write(float[] source) {

		var sourceLength = source.length;
		var sourceIndex = max(0, sourceLength - length);
		var writeLength = sourceLength - sourceIndex;
		var remainingLength = length - index;

		if (writeLength > remainingLength) {

			arraycopy(source, sourceIndex, array, index, remainingLength);
			arraycopy(source, sourceIndex + remainingLength, array, 0, writeLength - remainingLength);
			index = writeLength - remainingLength;

		} else if (writeLength < remainingLength){

			arraycopy(source, sourceIndex, array, index, writeLength);
			index += writeLength;

		} else {

			arraycopy(source, sourceIndex, array, index, writeLength);
			index = 0;
		}
	}
}
package com.github.achaaab.bragi.common;

import static java.lang.Math.floorMod;
import static java.lang.Math.min;
import static java.lang.System.arraycopy;

/**
 * @author Jonathan Guéhenneux
 * @since 0.0.7
 */
public class CircularFloatArray {

	private final float[] array;
	private final int length;

	private int writeIndex;
	private int readIndex;

	/**
	 * @param length array length, must be strictly positive
	 * @since 0.2.0
	 */
	public CircularFloatArray(int length) {

		if (length < 1) {
			throw new IllegalArgumentException("Circular array length must be strictly positive.");
		}

		this.length = length;

		array = new float[length];
		writeIndex = 0;
		readIndex = 0;
	}

	/**
	 * Reads elements from this circular array and write them to the given {@code target}.
	 *
	 * @param target array to write to
	 * @since 0.2.0
	 */
	public void read(float[] target) {

		var targetIndex = 0;

		while (targetIndex < target.length) {

			var readLength = min(length - readIndex, target.length - targetIndex);
			arraycopy(array, readIndex, target, targetIndex, readLength);
			targetIndex += readLength;
			readIndex = (readIndex + readLength) % length;
		}
	}

	/**
	 * Reads the last element that was written to this circular array.
	 *
	 * @return read element
	 * @since 0.2.0
	 */
	public float readLast() {
		return array[writeIndex == 0 ? length - 1 : writeIndex];
	}

	/**
	 * Reads the {@code target.length} lasts elements that were written to this circular array
	 * and write them to the given {@code target}.
	 *
	 * @param target array to write to
	 * @since 0.2.0
	 */
	public void readLast(float[] target) {

		var sourceIndex = floorMod(writeIndex - target.length, length);

		var targetIndex = 0;

		while (targetIndex < target.length) {

			var readLength = min(length - sourceIndex, target.length - targetIndex);
			arraycopy(array, sourceIndex, target, targetIndex, readLength);
			targetIndex += readLength;
			sourceIndex = (sourceIndex + readLength) % length;
		}
	}

	/**
	 * Writes 1 element to this circular array.
	 *
	 * @param element element to write
	 * @since 0.2.0
	 */
	public void write(float element) {

		array[writeIndex] = element;
		writeIndex = (writeIndex + 1) % length;
	}

	/**
	 * Reads elements from the given {@code source} and writes them to this circular array.
	 *
	 * @param source array to read from
	 * @since 0.2.0
	 */
	public void write(float[] source) {

		var sourceIndex = 0;

		while (sourceIndex < source.length) {

			var writeLength = min(length - writeIndex, source.length - sourceIndex);
			arraycopy(source, sourceIndex, array, writeIndex, writeLength);
			sourceIndex += writeLength;
			writeIndex = (writeIndex + writeLength) % length;
		}
	}
}

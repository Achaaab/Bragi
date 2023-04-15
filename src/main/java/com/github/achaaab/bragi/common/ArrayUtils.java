package com.github.achaaab.bragi.common;

import static java.util.Arrays.asList;
import static java.util.Arrays.fill;
import static java.util.Arrays.stream;

/**
 * @author Jonathan Guéhenneux
 * @since 0.0.7
 */
public class ArrayUtils {

	/**
	 * Creates a 2-dimension float array filled with initial value.
	 *
	 * @param dimension0 dimension 0 of the array, array.length
	 * @param dimension1 dimension 1 of the array, array[row].length
	 * @param initialValue value to be stored in all elements of the array
	 * @return created array
	 * @since 0.2.0
	 */
	public static float[][] createFloatArray(int dimension0, int dimension1, float initialValue) {

		var array = new float[dimension0][dimension1];
		stream(array).forEach(row -> fill(row, initialValue));

		return array;
	}

	/**
	 * @param array array to sum
	 * @return sum of elements in the given array
	 * @since 0.2.0
	 */
	public static float sum(float[] array) {

		var sum = 0.0f;

		for (var element : array) {
			sum += element;
		}

		return sum;
	}

	/**
	 * Tests if an array contains an element.
	 *
	 * @param array array to test
	 * @param element element to find
	 * @param <E> elements type
	 * @return whether the given array contains at least 1 element equals to the given element
	 * @since 0.2.0
	 */
	public static <E> boolean contains(E[] array, E element) {
		return asList(array).contains(element);
	}
}

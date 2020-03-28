package fr.guehenneux.bragi.common;

import java.util.Random;

import static java.lang.Integer.numberOfLeadingZeros;
import static java.lang.Math.ceil;
import static java.lang.Math.log;

/**
 * @author Jonathan Guéhenneux
 * @since 0.0.7
 */
public class RandomUtils {

	private static final Random RANDOM = new Random();

	/**
	 * @param successProbability success probability in [0.0, 1.0]
	 * @return number of trials before getting a success
	 */
	public static long getRandomGeometric(double successProbability) {

		long trials;

		if (successProbability == 0.5) {
			trials = searchRandomGeometricCoinFlip();
		} else if (successProbability > 0.05) {
			trials = searchRandomGeometric(successProbability);
		} else {
			trials = computeRandomGeometric(successProbability);
		}

		return trials;
	}

	/**
	 * @param successProbability success probability in [0.0, 1.0]
	 * @return number of trials before getting a success
	 */
	public static long computeRandomGeometric(double successProbability) {

		var random = RANDOM.nextFloat();
		return (long) ceil(log(1.0 - random) / log(1.0 - successProbability));
	}

	/**
	 * @param successProbability success probability in [0.0, 1.0]
	 * @return number of trials before getting a success
	 */
	public static long searchRandomGeometric(double successProbability) {

		var trials = 1L;
		var sum = successProbability;
		var product = successProbability;
		var failProbability = 1.0f - successProbability;

		var random = RANDOM.nextFloat();

		while (random > sum) {

			product *= failProbability;
			sum += product;
			trials++;
		}

		return trials;
	}

	/**
	 * @return number of trials to win a coin flip (with 0.5 of success probability)
	 */
	public static long searchRandomGeometricCoinFlip() {

		var trials = 1L;
		int random32Bits;
		int fails;

		do {

			random32Bits = RANDOM.nextInt();
			fails = numberOfLeadingZeros(random32Bits);
			trials += fails;

		} while (fails == 32);

		return trials;
	}
}
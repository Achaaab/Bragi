package fr.guehenneux.bragi.common;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Integer.numberOfLeadingZeros;
import static java.lang.Math.ceil;
import static java.lang.Math.log;

/**
 * Pseudorandom integer generator with geometric distribution based on a pseudorandom number generator with
 * uniform distribution.
 * <p>
 * Created initially to add Bernoulli trials.
 * <a href="https://en.wikipedia.org/wiki/Bernoulli_trial">Wikipedia</a>
 * <p>
 * {@link #computeRandomGeometric(double)} and {@link #searchRandomGeometric(double)}
 * are adaptations of NumPy library. However, there are 2 differences with NumPy implementation:
 * <ul>
 *     <li>numbers generated with random numbers are single precision</li>
 *     <li></li>
 * </ul>
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.7
 */
public class GeometricRandom {

	private Random uniformRandom;

	/**
	 * Creates a pseudorandom number generator with geometric distribution.
	 * The created {@code GeometricRandom} is based on a {@link ThreadLocalRandom},
	 * thus isolated to the current thread.
	 * <p>
	 * If you want to use a threadsafe {@code Random}, use the constructor {@link #GeometricRandom(Random)}.
	 *
	 * @see ThreadLocalRandom
	 * @since 0.0.8
	 */
	public GeometricRandom() {
		this(ThreadLocalRandom.current());
	}

	/**
	 * @param uniformRandom pseudorandom number generator with discrete uniform distribution
	 * @since 0.0.8
	 */
	public GeometricRandom(Random uniformRandom) {
		this.uniformRandom = uniformRandom;
	}

	/**
	 * @param successProbability success probability in {@code ]0.0, 1.0[}
	 * @return number of trials before getting a success
	 */
	public long getRandomGeometric(double successProbability) {

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
	 * @param successProbability success probability in {@code ]0.0, 1.0[}
	 * @return number of trials before getting a success
	 */
	public long computeRandomGeometric(double successProbability) {

		var randomNumber = uniformRandom.nextFloat();
		return (long) ceil(log(1.0 - randomNumber) / log(1.0 - successProbability));
	}

	/**
	 * @param successProbability success probability in {@code ]0.0, 1.0]}
	 * @return number of trials before getting a success
	 */
	public long searchRandomGeometric(double successProbability) {

		var trials = 1L;
		var sum = successProbability;
		var product = successProbability;
		var failureProbability = 1.0f - successProbability;

		var randomNumber = uniformRandom.nextFloat();

		while (randomNumber > sum) {

			product *= failureProbability;
			sum += product;
			trials++;
		}

		return trials;
	}

	/**
	 * @return number of trials to win a coin flip (with 1/2 of success probability)
	 */
	public long searchRandomGeometricCoinFlip() {

		var trials = 1L;
		int random32Bits;
		int failures;

		do {

			random32Bits = uniformRandom.nextInt();
			failures = numberOfLeadingZeros(random32Bits);
			trials += failures;

		} while (failures == 32);

		return trials;
	}
}
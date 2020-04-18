package com.github.achaaab.bragi.codec.flac.frame;

import com.github.achaaab.bragi.codec.flac.FlacException;
import com.github.achaaab.bragi.codec.flac.FlacInputStream;

import java.io.IOException;

/**
 * FLAC Rice method for residuals coding
 * <p>
 * @author Jonathan Guéhenneux
 * @since 0.1.8
 */
public abstract class Rice implements ResidualCodingMethod {

	protected final int parameterSize;
	protected final int escape;

	/**
	 * @param parameterSize number of bits of the Rice parameter
	 */
	Rice(int parameterSize) {

		this.parameterSize = parameterSize;

		escape = (1 << parameterSize) - 1;
	}

	@Override
	public void decode(FlacInputStream input, long[] samples, int warmUpSampleCount)
			throws IOException, FlacException {

		var partitionOrder = input.readUnsignedInteger(4);
		var partitionCount = 1 << partitionOrder;
		var sampleCount = samples.length;

		if (sampleCount % partitionCount != 0) {

			throw new FlacException("The number of samples (" + sampleCount + ") to decode is not divisible " +
					"by the number of Rice partitions (" + partitionCount + ").");
		}

		var partitionSize = sampleCount / partitionCount;

		for (var partitionIndex = 0; partitionIndex < partitionCount; partitionIndex++) {
			decodePartition(input, samples, partitionIndex, partitionSize, warmUpSampleCount);
		}
	}


	/**
	 * Decodes a Rice partition from the given FLAC input stream.
	 *
	 * @param input             FLAC input stream from which to decode a Rice partition
	 * @param samples           array in which to store decoded samples
	 * @param partitionIndex    index of the partition
	 * @param partitionSize     size of the partition (in samples)
	 * @param warmUpSampleCount number of warm-up samples already decoded
	 * @throws IOException I/O exception while reading from the given FLAC input stream
	 */
	private void decodePartition(FlacInputStream input, long[] samples,
	                             int partitionIndex, int partitionSize, int warmUpSampleCount) throws IOException {

		var start = partitionIndex * partitionSize;
		var end = start + partitionSize;

		if (partitionIndex == 0) {
			start += warmUpSampleCount;
		}

		var parameter = input.readUnsignedInteger(parameterSize);

		if (parameter < escape) {

			// Rice encoding

			for (var sampleIndex = start; sampleIndex < end; sampleIndex++) {
				samples[sampleIndex] = input.readRiceSignedInteger(parameter);
			}

		} else {

			// no encoding

			var sampleSize = input.readUnsignedInteger(5);

			for (var sampleIndex = start; sampleIndex < end; sampleIndex++) {
				samples[sampleIndex] = input.readSignedInt(sampleSize);
			}
		}
	}
}
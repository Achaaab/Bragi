package com.github.achaaab.bragi.codec.flac;

import com.github.achaaab.bragi.codec.flac.frame.FrameHeader;
import com.github.achaaab.bragi.codec.flac.header.StreamInfo;

import java.io.EOFException;
import java.io.IOException;

import static java.util.Arrays.fill;

/**
 * @author Project Nayuki
 * @author Jonathan Guéhenneux
 * @since 0.1.7
 */
public class FlacDecoder {

	private static final int[][] FIXED_PREDICTION_COEFFICIENTS = {
			{},
			{ 1 },
			{ 2, -1 },
			{ 3, -3, 1 },
			{ 4, -6, 4, -1 },
	};

	/**
	 * Decodes a FLAC header from the given FLAC input stream.
	 *
	 * @param input FLAC input stream to decode
	 * @return decoded FLAC header
	 * @throws IOException          I/O exception while decoding a FLAC header
	 * @throws FlacDecoderException if invalid or unsupported FLAC header is decoded
	 */
	public static FlacHeader decodeHeader(FlacInputStream input) throws IOException, FlacDecoderException {
		return new FlacHeader(input);
	}

	/**
	 * Decodes a FLAC frame.
	 *
	 * @param input      FLAC input stream to read from
	 * @param streamInfo global stream information
	 * @return decoded frame or {@code null} if the end of the stream is reached and there are no more frame to decode
	 * @throws IOException          I/O exception while decoding a frame
	 * @throws FlacDecoderException invalid flac frame
	 */
	public static int[][] decodeFrame(FlacInputStream input, StreamInfo streamInfo)
			throws IOException, FlacDecoderException {

		int[][] frame;

		try {

			var frameHeader = new FrameHeader(input, streamInfo);
			frame = frameHeader.decodeSubframes();
			input.alignToByte();
			input.readUnsignedInteger(16);

			// must add 128 to samples if sample size = 8 ?

		} catch (EOFException eofException) {

			frame = null;
		}

		return frame;
	}

	/**
	 * Decodes a subframe from the given FLAC input stream.
	 *
	 * @param input      FLAC input stream to decode
	 * @param sampleSize size of each sample in bits
	 * @param subframe   subframe samples
	 * @throws IOException          I/O exception while decoding a subframe
	 * @throws FlacDecoderException if invalid or unsupported subframe is decoded
	 */
	public static void decodeSubframe(FlacInputStream input, int sampleSize, long[] subframe)
			throws IOException, FlacDecoderException {

		input.readUnsignedInteger(1);

		var type = input.readUnsignedInteger(6);
		var shift = input.readUnsignedInteger(1);

		if (shift == 1) {

			while (input.readUnsignedInteger(1) == 0) {
				shift++;
			}
		}

		sampleSize -= shift;

		if (type == 0) {

			// constant coding

			fill(subframe, 0, subframe.length, input.readSignedInt(sampleSize));

		} else if (type == 1) {

			// verbatim coding

			for (var i = 0; i < subframe.length; i++) {
				subframe[i] = input.readSignedInt(sampleSize);
			}

		} else if (8 <= type && type <= 12) {

			// fixed coding

			decodeFixedPredictionSubframe(input, type - 8, sampleSize, subframe);

		} else if (32 <= type && type <= 63) {

			// LPC coding

			decodeLinearPredictiveCodingSubframe(input, type - 31, sampleSize, subframe);

		} else {

			throw new FlacDecoderException("unsupported subframe type: " + type);
		}

		for (var sampleIndex = 0; sampleIndex < subframe.length; sampleIndex++) {
			subframe[sampleIndex] <<= shift;
		}
	}

	/**
	 * Decodes a subframe that was encoded with Fixed Linear Predictor.
	 *
	 * @param input          FLAC input stream to read from
	 * @param predictorOrder order of linear predictor in [0, 4]
	 * @param sampleSize     size of each sample in bits
	 * @param samples        samples to decode
	 * @throws IOException          I/O exception while decoding a subframe
	 * @throws FlacDecoderException if an invalid or unsupported subframe is decoded
	 */
	private static void decodeFixedPredictionSubframe(
			FlacInputStream input, int predictorOrder, int sampleSize, long[] samples)
			throws IOException, FlacDecoderException {

		for (var i = 0; i < predictorOrder; i++) {
			samples[i] = input.readSignedInt(sampleSize);
		}

		decodeResiduals(input, predictorOrder, samples);
		restoreLinearPrediction(samples, FIXED_PREDICTION_COEFFICIENTS[predictorOrder], 0);
	}

	/**
	 * Decodes an LPC subframe (Linear Prediction Coding).
	 *
	 * @param input      FLAC input stream to read from
	 * @param order      linear prediction coding order in [1, 32]
	 * @param sampleSize size of each sample in bits
	 * @param samples    decoded subframe samples
	 * @throws IOException          I/O exception while decoding an LPC subframe
	 * @throws FlacDecoderException if invalid or unsupported LPC subframe is decoded
	 */
	private static void decodeLinearPredictiveCodingSubframe(
			FlacInputStream input, int order, int sampleSize, long[] samples)
			throws IOException, FlacDecoderException {

		for (var sampleIndex = 0; sampleIndex < order; sampleIndex++) {
			samples[sampleIndex] = input.readSignedInt(sampleSize);
		}

		var precision = input.readUnsignedInteger(4) + 1;
		var shift = input.readSignedInt(5);
		var coefficients = new int[order];

		for (var coefficientIndex = 0; coefficientIndex < coefficients.length; coefficientIndex++) {
			coefficients[coefficientIndex] = input.readSignedInt(precision);
		}

		decodeResiduals(input, order, samples);
		restoreLinearPrediction(samples, coefficients, shift);
	}

	/**
	 * @param input   FLAC input stream to read from
	 * @param warmUp
	 * @param samples samples to decode
	 * @throws IOException          I/O exception while decoding residuals
	 * @throws FlacDecoderException if invalid or unsupported residuals are decoded
	 */
	private static void decodeResiduals(FlacInputStream input, int warmUp, long[] samples)
			throws IOException, FlacDecoderException {

		var method = input.readUnsignedInteger(2);

		if (method >= 2) {
			throw new FlacDecoderException("unsupported residual coding method: " + method);
		}

		var paramBits = method == 0 ? 4 : 5;
		var escapeParam = method == 0 ? 0xF : 0x1F;

		var partitionOrder = input.readUnsignedInteger(4);
		var partitionCount = 1 << partitionOrder;

		if (samples.length % partitionCount != 0) {
			throw new FlacDecoderException("block size not divisible by number of Rice partitions");
		}

		var partitionSize = samples.length / partitionCount;

		for (var partitionIndex = 0; partitionIndex < partitionCount; partitionIndex++) {

			var start = partitionIndex * partitionSize + (partitionIndex == 0 ? warmUp : 0);
			var end = (partitionIndex + 1) * partitionSize;

			var param = input.readUnsignedInteger(paramBits);

			if (param < escapeParam) {

				for (var sampleIndex = start; sampleIndex < end; sampleIndex++) {
					samples[sampleIndex] = input.readRiceSignedInteger(param);
				}

			} else {

				var numBits = input.readUnsignedInteger(5);

				for (var sampleIndex = start; sampleIndex < end; sampleIndex++) {
					samples[sampleIndex] = input.readSignedInt(numBits);
				}
			}
		}
	}

	/**
	 * @param samples      subframe samples
	 * @param coefficients coefficients of quantized LPC
	 * @param shift        coefficients shift
	 */
	private static void restoreLinearPrediction(long[] samples, int[] coefficients, int shift) {

		for (var sampleIndex = coefficients.length; sampleIndex < samples.length; sampleIndex++) {

			var sum = 0L;

			for (var coefficientIndex = 0; coefficientIndex < coefficients.length; coefficientIndex++) {
				sum += samples[sampleIndex - 1 - coefficientIndex] * coefficients[coefficientIndex];
			}

			samples[sampleIndex] += sum >> shift;
		}
	}
}
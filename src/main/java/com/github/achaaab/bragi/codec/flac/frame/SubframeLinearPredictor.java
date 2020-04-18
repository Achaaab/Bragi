package com.github.achaaab.bragi.codec.flac.frame;

import com.github.achaaab.bragi.codec.flac.FlacException;
import com.github.achaaab.bragi.codec.flac.FlacInputStream;

import java.io.IOException;

import static com.github.achaaab.bragi.codec.flac.frame.ResidualCodingMethod.decode;

/**
 * FLAC subframe encoded with a linear predictor
 * <p>
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.8
 */
public abstract class SubframeLinearPredictor extends Subframe {

	protected final int order;
	protected int[] coefficients;
	protected int shift;

	/**
	 * Creates an abstract FLAC subframe with its header.
	 *
	 * @param frameHeader header of the enclosing frame
	 * @param header      header of this subframe
	 * @param input       FLAC input stream from which to read this subframe
	 * @param order       order of the linear predictor used to encode this subframe
	 * @param extraBit    whether to add an extra bit (used for difference channel)
	 * @throws IOException   I/O exception while reading from the given FLAC input stream
	 * @throws FlacException if invalid subframe is decoded
	 */
	public SubframeLinearPredictor(FrameHeader frameHeader, SubframeHeader header, FlacInputStream input,
	                               int order, boolean extraBit) throws IOException, FlacException {

		super(frameHeader, header, input, extraBit);

		this.order = order;

		// warm-up samples

		for (var sampleIndex = 0; sampleIndex < order; sampleIndex++) {
			samples[sampleIndex] = input.readSignedInt(sampleSize);
		}

		configure();
		decodeResiduals();
		restoreLinearPrediction();
		insertWastedBits();
	}

	/**
	 * Configures the linear predictor.
	 * Get coefficients and optional shift in bits.
	 */
	protected abstract void configure() throws IOException;

	/**
	 * @throws IOException   I/O exception while reading from the FLAC input stream
	 * @throws FlacException if invalid method or residuals are decoded
	 */
	protected void decodeResiduals() throws IOException, FlacException {

		var method = decode(input);
		method.decode(input, samples, order);
	}

	/**
	 * Restores linear prediction.
	 */
	protected void restoreLinearPrediction() {

		var sampleCount = samples.length;
		var coefficientCount = coefficients.length;

		for (var sampleIndex = coefficientCount; sampleIndex < sampleCount; sampleIndex++) {

			var sum = 0L;

			for (var coefficientIndex = 0; coefficientIndex < coefficientCount; coefficientIndex++) {
				sum += samples[sampleIndex - 1 - coefficientIndex] * coefficients[coefficientIndex];
			}

			samples[sampleIndex] += sum >> shift;
		}
	}
}
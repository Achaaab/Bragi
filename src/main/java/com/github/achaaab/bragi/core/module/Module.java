package com.github.achaaab.bragi.core.module;

import com.github.achaaab.bragi.common.AbstractNamedEntity;
import com.github.achaaab.bragi.core.connection.Input;
import com.github.achaaab.bragi.core.connection.Output;
import com.github.achaaab.bragi.core.connection.PrimaryInput;
import com.github.achaaab.bragi.core.connection.PrimaryOutput;
import com.github.achaaab.bragi.core.connection.SecondaryInput;
import com.github.achaaab.bragi.core.connection.SecondaryOutput;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.round;
import static java.lang.System.nanoTime;
import static java.lang.Thread.sleep;
import static java.util.Arrays.stream;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * A {@link Module} has inputs and outputs. It reads samples from inputs, compute output samples from input samples and
 * tuning and write computed samples to outputs.
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.9
 */
public abstract class Module extends AbstractNamedEntity implements Runnable {

	private static final Logger LOGGER = getLogger(Module.class);

	protected final List<Input> inputs;
	protected final List<Output> outputs;

	protected boolean started;
	protected double computingFrameRate;

	/**
	 * @param name name of the module
	 */
	public Module(String name) {

		super(name);

		inputs = new ArrayList<>();
		outputs = new ArrayList<>();

		started = false;
		computingFrameRate = 0.0;
	}

	/**
	 * @return inputs of this module
	 */
	public List<Input> getInputs() {
		return inputs;
	}

	/**
	 * @return outputs of this module
	 */
	public List<Output> getOutputs() {
		return outputs;
	}

	/**
	 * @return first input (by convention it is the main input), {@code null} if there is no input
	 */
	public Input getInput() {
		return inputs.isEmpty() ? null : inputs.get(0);
	}

	/**
	 * @return first output (by convention it is the main output), {@code null} if there is no output
	 */
	public Output getOutput() {
		return outputs.isEmpty() ? null : outputs.get(0);
	}

	/**
	 * Connects the outputs of this module to the inputs of given module.
	 *
	 * @param module module to connect to
	 * @since 0.1.6
	 */
	public void connectOutputs(Module module) {

		var channelCount = outputs.size();

		for (var channel = 0; channel < channelCount; channel++) {

			var output = getOutputs().get(channel);
			var input = module.getInputs().get(channel);

			output.connect(input);
		}
	}

	/**
	 * Connects the outputs of this module to the main input of given modules.
	 *
	 * @param modules modules to connect to
	 * @since 0.1.6
	 */
	public void connectOutputs(Module... modules) {

		var channelCount = modules.length;

		for (var channel = 0; channel < channelCount; channel++) {

			var output = getOutputs().get(channel);
			modules[channel].connect(output);
		}
	}

	/**
	 * Connects the main input of given modules to the inputs of this module.
	 *
	 * @param modules modules to connect from
	 * @since 0.1.6
	 */
	public void connectInputs(Module... modules) {

		var channelCount = modules.length;

		for (var channel = 0; channel < channelCount; channel++) {

			var input = getInputs().get(channel);
			modules[channel].connect(input);
		}
	}

	/**
	 * Connects the main output of this module to the main input of given module.
	 *
	 * @param module module to connect to
	 */
	public void connect(Module module) {

		var input = module.getInput();
		connect(input);
	}

	/**
	 * Connects the main output of this module to the main input of given modules.
	 *
	 * @param modules modules to connect to
	 * @since 0.1.6
	 */
	public void connect(Module... modules) {
		stream(modules).forEach(this::connect);
	}

	/**
	 * Connects the main output of this module to the given input.
	 *
	 * @param input input to connect
	 * @since 0.1.6
	 */
	public void connect(Input input) {

		var output = getOutput();
		output.connect(input);
	}

	/**
	 * Connects the given output to the main input of this module.
	 *
	 * @param output output to connect from
	 * @since 0.1.6
	 */
	public void connect(Output output) {

		var input = getInput();
		output.connect(input);
	}

	/**
	 * Add an input to this module.
	 *
	 * @param name input name
	 * @return created input
	 */
	protected PrimaryInput addPrimaryInput(String name) {

		var input = new PrimaryInput(name);
		inputs.add(input);
		return input;
	}

	/**
	 * Create and add a secondary input to this module.
	 *
	 * @param name name of the secondary input to create.
	 * @return created and added secondary input
	 */
	protected SecondaryInput addSecondaryInput(String name) {

		var secondaryInput = new SecondaryInput(name);
		inputs.add(secondaryInput);
		return secondaryInput;
	}

	/**
	 * Create and add a primary output to this module.
	 *
	 * @param name name of the primary output
	 * @return created and added primary output
	 */
	protected Output addPrimaryOutput(String name) {

		var output = new PrimaryOutput(name);
		outputs.add(output);
		return output;
	}

	/**
	 * Create and add a secondary output to this module.
	 *
	 * @param name name of the secondary output
	 * @return created and added secondary output
	 */
	protected Output addSecondaryOutput(String name) {

		var output = new SecondaryOutput(name);
		outputs.add(output);
		return output;
	}

	/**
	 * Start the module in a new thread.
	 */
	protected void start() {

		started = true;

		new Thread(this, name).start();

		LOGGER.info("module \"" + name + "\" started");
	}

	/**
	 * Stop the module.
	 */
	protected void stop() {
		started = false;
	}

	@Override
	public void run() {

		while (started) {

			try {

				if (computingFrameRate > 0.0) {

					var startTime = nanoTime();
					var computedFrameCount = compute();

					waitComputeTime(startTime, computedFrameCount);

				} else {

					compute();
				}

			} catch (InterruptedException cause) {

				throw new ModuleExecutionException(cause);
			}
		}
	}

	/**
	 * Sleeps if necessary until the configured compute time is elapsed.
	 *
	 * @param startTime          compute start nano-time
	 * @param computedFrameCount number of computed frames
	 * @throws InterruptedException if the current thread is interrupted while waiting for compute time
	 */
	private void waitComputeTime(long startTime, int computedFrameCount) throws InterruptedException {

		var endTime = nanoTime();
		var duration = endTime - startTime;
		var expectedDuration = round(1_000_000_000L * computedFrameCount / computingFrameRate);

		if (duration < expectedDuration) {

			var waitTime = expectedDuration - duration;
			sleep(waitTime / 1_000_000, (int) (waitTime % 1_000_000));

		} else {

			LOGGER.warn("computing rate lower than configured");
		}
	}

	/**
	 * @param computingSampleRate number of frames to compute per second, {@code 0.0} for maximal speed computing
	 */
	public void setComputingFrameRate(double computingSampleRate) {
		this.computingFrameRate = computingSampleRate;
	}

	/**
	 * Typically, modules computing is done in 4 optional steps :
	 * <ol start="0">
	 *     <li>Read current tuning.</li>
	 *     <li>Read chunks from inputs.</li>
	 *     <li>Compute chunks from input chunks and tuning.</li>
	 *     <li>Write chunks to outputs.</li>
	 * </ol>
	 *
	 * @return number of computed frames
	 * @throws InterruptedException if computing was interrupted
	 */
	protected abstract int compute() throws InterruptedException;
}
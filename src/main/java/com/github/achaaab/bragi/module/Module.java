package com.github.achaaab.bragi.module;

import com.github.achaaab.bragi.common.ModuleExecutionException;
import com.github.achaaab.bragi.common.connection.Input;
import com.github.achaaab.bragi.common.connection.Output;
import com.github.achaaab.bragi.common.connection.PrimaryInput;
import com.github.achaaab.bragi.common.connection.PrimaryOutput;
import com.github.achaaab.bragi.common.connection.SecondaryInput;
import com.github.achaaab.bragi.common.connection.SecondaryOutput;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.round;
import static java.lang.System.nanoTime;
import static java.lang.Thread.sleep;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * A {@link Module} has inputs and outputs. It reads samples from inputs, compute output samples from input samples and
 * tuning and write output samples to outputs.
 *
 * @author Jonathan Guéhenneux
 * @since 0.0.9
 */
public abstract class Module implements Runnable {

	private static final Logger LOGGER = getLogger(Module.class);

	protected final String name;
	protected final List<Input> inputs;
	protected final List<Output> outputs;

	protected boolean started;
	protected double computingFrameRate;

	/**
	 * @param name name of the module
	 */
	public Module(String name) {

		this.name = name;

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
	 * @return the first output (by convention it is the main output), {@code null} if there is no output
	 */
	public Output getOutput() {
		return outputs.isEmpty() ? null : outputs.get(0);
	}

	/**
	 * Connect outputs of this module to inputs of output modules.
	 *
	 * @param modules output modules
	 */
	public void connectTo(Module... modules) {

		var channelCount = modules.length;

		for (var channel = 0; channel < channelCount; channel++) {
			getOutputs().get(channel).connect(modules[channel].getInput());
		}
	}

	/**
	 * Connect outputs of input modules to inputs of this module.
	 *
	 * @param modules input modules
	 */
	public void connectFrom(Module... modules) {

		var channelCount = modules.length;

		for (var channel = 0; channel < channelCount; channel++) {
			modules[channel].getOutput().connect(getInputs().get(channel));
		}
	}

	/**
	 * Connect the main output of this module to the main input of the given output module.
	 *
	 * @param module output module
	 */
	public void connectTo(Module module) {
		getOutput().connect(module.getInput());
	}

	/**
	 * Connect the first output of this module to the given input.
	 *
	 * @param input input to connect
	 */
	public void connect(Input input) {
		getOutput().connect(input);
	}

	/**
	 * Connect the main output of this module to given inputs.
	 *
	 * @param inputs inputs to connect
	 */
	public void connect(Iterable<Input> inputs) {
		inputs.forEach(this::connect);
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
	 * @return name of this module
	 */
	public String getName() {
		return name;
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
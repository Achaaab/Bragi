package fr.guehenneux.bragi.module.model;

import fr.guehenneux.bragi.connection.Input;
import fr.guehenneux.bragi.connection.Output;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.round;
import static java.lang.System.nanoTime;
import static java.lang.Thread.sleep;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Jonathan Guéhenneux
 */
public abstract class Module implements Runnable {

	private static final Logger LOGGER = getLogger(Module.class);

	protected String name;
	protected List<Input> inputs;
	protected List<Output> outputs;

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
	 * @return module inputs
	 */
	public List<Input> getInputs() {
		return inputs;
	}

	/**
	 * @return module outputs
	 */
	public List<Output> getOutputs() {
		return outputs;
	}

	/**
	 * @return the first input (by convention it is the main input), {@code null} if there is no input
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
	protected Input addInput(String name) {

		var input = new Input(name);
		inputs.add(input);
		return input;
	}

	/**
	 * Add an output to this module.
	 *
	 * @param name output name
	 * @return created output
	 */
	protected Output addOutput(String name) {

		var output = new Output(name);
		outputs.add(output);
		return output;
	}

	/**
	 * @return module name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name module name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Start the module in a new thread.
	 */
	protected void start() {

		new Thread(this, name).start();

		started = true;

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
					var endTime = nanoTime();

					var duration = endTime - startTime;
					var expectedDuration = round(1_000_000_000L * computedFrameCount / computingFrameRate);

					if (duration < expectedDuration) {

						var waitTime = expectedDuration - duration;
						sleep(waitTime / 1_000_000, (int) (waitTime % 1_000_000));

					} else {

						LOGGER.warn("computing rate lower than configured");
					}

				} else {

					compute();
				}

			} catch (InterruptedException cause) {
				throw new RuntimeException(cause);
			}
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
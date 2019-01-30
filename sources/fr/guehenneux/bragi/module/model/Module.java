package fr.guehenneux.bragi.module.model;

import fr.guehenneux.bragi.connection.Input;
import fr.guehenneux.bragi.connection.Output;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jonathan Guéhenneux
 */
public abstract class Module implements Runnable {

	private static final Logger LOGGER = LogManager.getLogger();

	protected String name;
	protected List<Input> inputs;
	protected List<Output> outputs;

	/**
	 * @param name
	 */
	public Module(String name) {

		this.name = name;

		inputs = new ArrayList<>();
		outputs = new ArrayList<>();
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
	public final Input getInput() {
		return inputs.isEmpty() ? null : inputs.get(0);
	}

	/**
	 * @return the first output (by convention it is the main output), {@code null} if there is no output
	 */
	public final Output getOutput() {
		return outputs.isEmpty() ? null : outputs.get(0);
	}

	/**
	 * Connect outputs of this module to inputs of given module.
	 *
	 * @param module output module
	 */
	public void connect(Module module) {

		int outputCount = outputs.size();
		int inputCount = module.inputs.size();

		int index = 0;
		Output output;
		Input input;

		while (index < outputCount && index < inputCount) {

			output = outputs.get(index);
			input = module.inputs.get(index++);

			output.connect(input);
		}
	}

	/**
	 * Add an input to this module.
	 *
	 * @param name input name
	 * @return created input
	 */
	protected Input addInput(String name) {

		Input input = new Input(name);
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

		Output output = new Output(name);
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

		Thread moduleThread = new Thread( this, name);
		moduleThread.start();

		LOGGER.info("module \"" + name + "\" started");
	}

	@Override
	public void run() {

		while (true) {

			try {
				compute();
			} catch (InterruptedException cause) {
				throw new RuntimeException(cause);
			}
		}
	}

	/**
	 * @throws InterruptedException if computing was interrupted
	 */
	protected abstract void compute() throws InterruptedException;
}
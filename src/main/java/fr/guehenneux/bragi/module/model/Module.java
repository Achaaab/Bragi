package fr.guehenneux.bragi.module.model;

import fr.guehenneux.bragi.connection.Input;
import fr.guehenneux.bragi.connection.Output;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Jonathan Guéhenneux
 */
public abstract class Module implements Runnable {

	private static final Logger LOGGER = getLogger(Module.class);

	protected String name;
	protected List<Input> inputs;
	protected List<Output> outputs;

	/**
	 * @param name name of the module
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
	 * Connect outputs of this module to inputs of given module.
	 *
	 * @param module output module
	 */
	public void connectTo(Module module) {

		for (int index = 0; index < outputs.size(); index++) {

			var output = outputs.get(index);
			var input = module.inputs.get(index);
			output.connect(input);
		}
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

		new Thread(this, name).start();

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
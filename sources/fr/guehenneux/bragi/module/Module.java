package fr.guehenneux.bragi.module;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jonathan Guéhenneux
 */
public abstract class Module implements Runnable {

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
	 * Add an input to this module.
	 *
	 * @return created input
	 */
	protected Input addInput() {

		Input input = new Input();
		inputs.add(input);
		return input;
	}

	/**
	 * Add an output to this module.
	 *
	 * @return created output
	 */
	protected Output addOutut() {

		Output output = new Output();
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
	public void start() {

		Thread moduleThread = new Thread( this, name);
		moduleThread.start();
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
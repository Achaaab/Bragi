package com.github.achaaab.bragi.core;

import com.github.achaaab.bragi.common.AbstractNamedEntity;
import com.github.achaaab.bragi.core.configuration.Configuration;
import com.github.achaaab.bragi.core.module.Module;
import com.github.achaaab.bragi.gui.SynthesizerView;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static javax.swing.SwingUtilities.invokeAndWait;

/**
 * @author Jonathan Guéhenneux
 * @since 0.1.8
 */
public class Synthesizer extends AbstractNamedEntity {

	private static final String DEFAULT_NAME = "synthesizer";

	private final Configuration configuration;
	private final List<Module> modules;

	private SynthesizerView view;

	/**
	 * Creates a synthesizer.
	 *
	 * @since 0.2.0
	 */
	public Synthesizer() {

		super(DEFAULT_NAME);

		configuration = new Configuration(this);
		modules = new ArrayList<>();

		try {
			invokeAndWait(() -> view = new SynthesizerView(this));
		} catch (InterruptedException | InvocationTargetException cause) {
			throw new SynthesizerCreationException(cause);
		}
	}

	/**
	 * @return configuration of this synthesizer
	 * @since 0.2.0
	 */
	public Configuration configuration() {
		return configuration;
	}

	/**
	 * Called when the configuration has changed.
	 *
	 * @since 0.2.0
	 */
	public void configure() {
		modules.forEach(Module::configure);
	}

	/**
	 * Adds a module to this synthesizer.
	 *
	 * @param module module to add
	 * @since 0.2.0
	 */
	public void addModule(Module module) {

		modules.add(module);

		module.setSynthesizer(this);
		module.start();

		view.display(module);
	}

	/**
	 * Add the given module to this synthesizer and recursively add all its input modules and output modules.
	 *
	 * @param module module to add
	 * @since 0.2.0
	 */
	public void addChain(Module module) {
		addChain(module, new HashSet<>());
	}

	/**
	 * Add the given module to this synthesizer and recursively add all its input modules and output modules.
	 *
	 * @param module module to add
	 * @param addedModules modules already added, that will be ignored
	 * @since 0.2.0
	 */
	public void addChain(Module module, Set<Module> addedModules) {

		if (!addedModules.contains(module)) {

			addModule(module);
			addedModules.add(module);

			for (var inputModule : module.inputModules()) {
				addChain(inputModule, addedModules);
			}

			for (var outputModule : module.outputModules()) {
				addChain(outputModule, addedModules);
			}
		}
	}
}

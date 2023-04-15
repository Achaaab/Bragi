package com.github.achaaab.bragi.gui;

import com.github.achaaab.bragi.core.Synthesizer;
import com.github.achaaab.bragi.core.configuration.LineConfiguration;
import com.github.achaaab.bragi.core.module.Module;
import com.github.achaaab.bragi.gui.configuration.LineConfigurationView;
import org.slf4j.Logger;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import static com.github.achaaab.bragi.gui.common.ViewScale.scale;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Jonathan Guéhenneux
 * @since 0.2.0
 */
public class SynthesizerView extends JPanel {

	private static final Logger LOGGER = getLogger(SynthesizerView.class);

	private static final Color MODULES_PANEL_BACKGROUND_COLOR = new Color(48, 48, 48);
	private static final Dimension MODULES_PANEL_DIMENSION = scale(new Dimension(3200, 1800));

	private final Synthesizer model;

	private final LineConfigurationView inputConfigurationView;
	private final LineConfigurationView outputConfigurationView;

	private final JFrame inputConfigurationFrame;
	private final JFrame outputConfigurationFrame;

	private final JDesktopPane modulesPanel;

	/**
	 * @param model synthesizer model
	 * @since 0.2.0
	 */
	public SynthesizerView(Synthesizer model) {

		LOGGER.info("creating view for the synthesizer: {}", model);

		this.model = model;

		var menuBar = new JMenuBar();

		var configurationMenu = new JMenu("Configuration");
		configurationMenu.setMnemonic('C');
		menuBar.add(configurationMenu);

		var inputConfigurationInput = new JMenuItem("Input");
		var outputConfigurationInput = new JMenuItem("Output");

		inputConfigurationInput.setMnemonic('I');
		outputConfigurationInput.setMnemonic('O');

		configurationMenu.add(inputConfigurationInput);
		configurationMenu.add(outputConfigurationInput);

		inputConfigurationView = new LineConfigurationView();
		outputConfigurationView = new LineConfigurationView();

		inputConfigurationFrame = new JFrame("Input configuration");
		inputConfigurationFrame.setContentPane(inputConfigurationView);

		outputConfigurationFrame = new JFrame("Output configuration");
		outputConfigurationFrame.setContentPane(outputConfigurationView);

		inputConfigurationInput.addActionListener(this::inputConfigurationSelected);
		outputConfigurationInput.addActionListener(this::outputConfigurationSelected);

		inputConfigurationView.onOk(this::inputConfigurationChanged);
		outputConfigurationView.onOk(this::outputConfigurationChanged);

		inputConfigurationView.onCancel(() -> inputConfigurationFrame.setVisible(false));
		outputConfigurationView.onCancel(() -> outputConfigurationFrame.setVisible(false));

		modulesPanel = new JDesktopPane();
		modulesPanel.setPreferredSize(MODULES_PANEL_DIMENSION);
		modulesPanel.setBackground(MODULES_PANEL_BACKGROUND_COLOR);

		var layout = new BorderLayout();
		setLayout(layout);
		add(menuBar, NORTH);
		add(new JScrollPane(modulesPanel), CENTER);

		scale(this);
		scale(inputConfigurationFrame);
		scale(outputConfigurationFrame);

		var frame = new JFrame("Synthesizer");
		frame.setContentPane(this);
		frame.pack();
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.setVisible(true);

		LOGGER.info("view created for the synthesizer: {}", model);
	}

	/**
	 * Displays the module view, if it is not {@code null}.
	 *
	 * @param module module to display
	 * @since 0.2.0
	 */
	public void display(Module module) {

		var moduleView = module.view();

		if (moduleView != null) {

			LOGGER.info("adding view of the module {}", module);

			var moduleFrame = new JInternalFrame(module.name());
			moduleFrame.add(moduleView);
			modulesPanel.add(moduleFrame);
			scale(moduleFrame);

			moduleFrame.pack();
			moduleFrame.setResizable(true);
			moduleFrame.setVisible(true);

			LOGGER.info("view of the module {} added", module);
		}
	}

	/**
	 * Called when the user defined a new configuration for the input line.
	 *
	 * @param inputConfiguration new configuration of the input line
	 * @since 0.2.0
	 */
	private void inputConfigurationChanged(LineConfiguration inputConfiguration) {

		model.configuration().setInputConfiguration(inputConfiguration);
		inputConfigurationFrame.setVisible(false);
	}

	/**
	 * Called when the user defined a new configuration for the output line.
	 *
	 * @param outputConfiguration new configuration of the output line
	 * @since 0.2.0
	 */
	private void outputConfigurationChanged(LineConfiguration outputConfiguration) {

		model.configuration().setOutputConfiguration(outputConfiguration);
		outputConfigurationFrame.setVisible(false);
	}

	/**
	 * @param event input configuration selection event
	 * @since 0.2.0
	 */
	private void inputConfigurationSelected(ActionEvent event) {

		inputConfigurationView.display(model.configuration().getInputConfiguration());
		inputConfigurationFrame.pack();
		inputConfigurationFrame.setVisible(true);
	}

	/**
	 * @param event output configuration selection event
	 * @since 0.2.0
	 */
	private void outputConfigurationSelected(ActionEvent event) {

		outputConfigurationView.display(model.configuration().getOutputConfiguration());
		outputConfigurationFrame.pack();
		outputConfigurationFrame.setVisible(true);
	}
}

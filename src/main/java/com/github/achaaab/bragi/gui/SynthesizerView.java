package com.github.achaaab.bragi.gui;

import com.github.achaaab.bragi.core.Synthesizer;
import com.github.achaaab.bragi.core.configuration.LineConfiguration;
import com.github.achaaab.bragi.gui.configuration.LineConfigurationView;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import static com.github.achaaab.bragi.gui.common.ViewScale.scale;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

/**
 * @author Jonathan Guéhenneux
 */
public class SynthesizerView extends JPanel {

	private final Synthesizer model;

	private final LineConfigurationView inputConfigurationView;
	private final LineConfigurationView outputConfigurationView;

	private final JFrame inputConfigurationFrame;
	private final JFrame outputConfigurationFrame;

	/**
	 * @param model synthesizer model
	 */
	public SynthesizerView(Synthesizer model) {

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
		scale(inputConfigurationFrame);
		inputConfigurationFrame.pack();

		outputConfigurationFrame = new JFrame("Output configuration");
		outputConfigurationFrame.setContentPane(outputConfigurationView);
		scale(outputConfigurationFrame);
		outputConfigurationFrame.pack();

		inputConfigurationInput.addActionListener(this::inputConfigurationSelected);
		outputConfigurationInput.addActionListener(this::outputConfigurationSelected);

		inputConfigurationView.onOk(this::inputConfigurationChanged);
		outputConfigurationView.onOk(this::outputConfigurationChanged);

		inputConfigurationView.onCancel(() -> inputConfigurationFrame.setVisible(false));
		outputConfigurationView.onCancel(() -> outputConfigurationFrame.setVisible(false));

		var layout = new BorderLayout();
		setLayout(layout);

		add(menuBar, NORTH);

		var pane = new JDesktopPane();
		var internalFrame = new JInternalFrame();
		internalFrame.setSize(320, 180);
		internalFrame.setVisible(true);
		pane.add(internalFrame);

		add(pane, CENTER);

		scale(this);

		var frame = new JFrame("Synthesizer");
		frame.setContentPane(this);
		frame.setSize(1600, 900);
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	/**
	 * Called when the user defined a new configuration for the input line.
	 *
	 * @param inputConfiguration new configuration of the input line
	 */
	private void inputConfigurationChanged(LineConfiguration inputConfiguration) {

		model.configuration().setInputConfiguration(inputConfiguration);
		inputConfigurationFrame.setVisible(false);
	}

	/**
	 * Called when the user defined a new configuration for the output line.
	 *
	 * @param outputConfiguration new configuration of the output line
	 */
	private void outputConfigurationChanged(LineConfiguration outputConfiguration) {

		model.configuration().setOutputConfiguration(outputConfiguration);
		outputConfigurationFrame.setVisible(false);
	}

	/**
	 * @param event input configuration selection event
	 */
	private void inputConfigurationSelected(ActionEvent event) {

		inputConfigurationView.display(model.configuration().getInputConfiguration());
		inputConfigurationFrame.pack();
		inputConfigurationFrame.setVisible(true);
	}

	/**
	 * @param event output configuration selection event
	 */
	private void outputConfigurationSelected(ActionEvent event) {

		outputConfigurationView.display(model.configuration().getOutputConfiguration());
		outputConfigurationFrame.pack();
		outputConfigurationFrame.setVisible(true);
	}
}
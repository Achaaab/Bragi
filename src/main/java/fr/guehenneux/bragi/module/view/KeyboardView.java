package fr.guehenneux.bragi.module.view;

import fr.guehenneux.bragi.module.model.Keyboard;
import fr.guehenneux.bragi.wave.Pulse;
import fr.guehenneux.bragi.wave.ReverseSawtooth;
import fr.guehenneux.bragi.wave.Sawtooth;
import fr.guehenneux.bragi.wave.Sine;
import fr.guehenneux.bragi.wave.Triangle;
import fr.guehenneux.bragi.wave.Waveform;

import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;
import static javax.swing.KeyStroke.getKeyStroke;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

/**
 * @author Jonathan Guéhenneux
 */
public class KeyboardView extends JPanel {

	/**
	 * @param model
	 */
	public KeyboardView(Keyboard model) {

		var waveforms = new Waveform[]{
				Sine.INSTANCE,
				Triangle.INSTANCE,
				Sawtooth.INSTANCE,
				ReverseSawtooth.INSTANCE,
				Pulse.SQUARE,
				Pulse.PULSE_4,
				Pulse.PULSE_8
		};

		var waveformsComboBox = new JComboBox<>(waveforms);

		waveformsComboBox.addActionListener(actionEvent ->
				model.setWaveform((Waveform) waveformsComboBox.getSelectedItem()));

		var keys = model.getKeys();
		var keyCount = keys.size();

		var keysPanel = new JPanel();
		keysPanel.setLayout(new GridLayout(1, keyCount));

		keysPanel.setFocusTraversalKeysEnabled(false);
		setFocusTraversalKeysEnabled(false);

		for (var key : keys) {

			var keyView = new KeyView(key, model);
			keysPanel.add(keyView);

			var code = key.getCode();
			var keyPressed = getKeyStroke(code, 0, false);
			var keyReleased = getKeyStroke(code, 0, true);

			getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyPressed, key.toString() + "_pressed");
			getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyReleased, key.toString() + "_released");

			getActionMap().put(key.toString() + "_pressed", new AbstractAction() {

				@Override
				public void actionPerformed(ActionEvent actionEvent) {
					keyView.press();
				}
			});

			getActionMap().put(key.toString() + "_released", new AbstractAction() {

				@Override
				public void actionPerformed(ActionEvent actionEvent) {
					keyView.release();
				}
			});
		}

		setLayout(new BorderLayout());
		add(waveformsComboBox, NORTH);
		add(keysPanel, CENTER);

		var frame = new JFrame(model.getName());
		frame.add(this);
		frame.pack();
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
package fr.guehenneux.bragi.module.view;

import fr.guehenneux.bragi.module.model.Key;
import fr.guehenneux.bragi.module.model.Keyboard;
import fr.guehenneux.bragi.wave.Pulse;
import fr.guehenneux.bragi.wave.Sawtooth;
import fr.guehenneux.bragi.wave.Sine;
import fr.guehenneux.bragi.wave.Triangle;
import fr.guehenneux.bragi.wave.Waveform;

import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.List;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;

/**
 * @author Jonathan Guéhenneux
 */
public class KeyboardView extends JPanel {

	/**
	 * @param model
	 */
	public KeyboardView(Keyboard model) {

		Waveform[] waveforms = {Sine.INSTANCE, Triangle.INSTANCE, Sawtooth.INSTANCE, Pulse.SQUARE, Pulse.PULSE_25, Pulse.PULSE_125};
		JComboBox<Waveform> waveformsComboBox = new JComboBox<>(waveforms);
		waveformsComboBox.addActionListener(actionEvent -> model.setWaveform((Waveform) waveformsComboBox.getSelectedItem()));

		List<Key> keys = model.getKeys();
		int keyCount = keys.size();

		JPanel keysPanel = new JPanel();
		keysPanel.setLayout(new GridLayout(1, keyCount));

		for (Key key : keys) {

			KeyView keyView = new KeyView(key, model);
			keysPanel.add(keyView);
			int code = key.getCode();
			KeyStroke keyPressed = KeyStroke.getKeyStroke(code, 0, false);
			KeyStroke keyReleased = KeyStroke.getKeyStroke(code, 0, true);
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
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
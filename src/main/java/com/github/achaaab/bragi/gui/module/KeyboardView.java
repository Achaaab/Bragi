package com.github.achaaab.bragi.gui.module;

import com.github.achaaab.bragi.module.Keyboard;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import static java.awt.BorderLayout.CENTER;
import static javax.swing.KeyStroke.getKeyStroke;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

/**
 * keyboard Swing view
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.0
 */
public class KeyboardView extends JPanel {

	/**
	 * @param model keyboard model
	 */
	public KeyboardView(Keyboard model) {

		var keys = model.getKeys();
		var keyCount = keys.size();

		var keysPanel = new JPanel();
		keysPanel.setLayout(new GridLayout(1, keyCount));

		keysPanel.setFocusTraversalKeysEnabled(false);
		setFocusTraversalKeysEnabled(false);

		for (var key : keys) {

			var keyView = new KeyView(key, model);
			keysPanel.add(keyView);

			var code = key.code();
			var keyPressed = getKeyStroke(code, 0, false);
			var keyReleased = getKeyStroke(code, 0, true);

			getInputMap(WHEN_IN_FOCUSED_WINDOW).put(keyPressed, key.toString() + "_pressed");
			getInputMap(WHEN_IN_FOCUSED_WINDOW).put(keyReleased, key.toString() + "_released");

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
		add(keysPanel, CENTER);

		var frame = new JFrame(model.getName());
		frame.setContentPane(this);
		frame.pack();
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
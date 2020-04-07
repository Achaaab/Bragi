package com.github.achaaab.bragi.gui.module;

import com.github.achaaab.bragi.module.producer.Keyboard;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import static javax.swing.KeyStroke.getKeyStroke;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

/**
 * keyboard Swing view
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.0
 */
public class KeyboardView extends JPanel {

	private static final String KEY_PRESSED_SUFFIX = "_pressed";
	private static final String KEY_RELEASED_SUFFIX = "_released";

	/**
	 * @param model keyboard model
	 */
	public KeyboardView(Keyboard model) {

		var keys = model.getKeys();
		var keyCount = keys.size();

		setLayout(new GridLayout(1, keyCount));

		var inputMap = getInputMap(WHEN_IN_FOCUSED_WINDOW);
		var actionMap = getActionMap();

		setFocusTraversalKeysEnabled(false);

		for (var key : keys) {

			var keyView = new KeyView(key, model);
			add(keyView);

			var code = key.code();
			var name = key.name();

			var keyPressed = getKeyStroke(code, 0, false);
			var keyReleased = getKeyStroke(code, 0, true);

			var keyPressedKey = name + KEY_PRESSED_SUFFIX;
			var keyReleasedKey = name + KEY_RELEASED_SUFFIX;

			inputMap.put(keyPressed, keyPressedKey);
			inputMap.put(keyReleased, keyReleasedKey);

			actionMap.put(keyPressedKey, new AbstractAction() {

				@Override
				public void actionPerformed(ActionEvent event) {
					keyView.press();
				}
			});

			actionMap.put(keyReleasedKey, new AbstractAction() {

				@Override
				public void actionPerformed(ActionEvent event) {
					keyView.release();
				}
			});
		}

		var frame = new JFrame(model.name());
		frame.setContentPane(this);
		frame.pack();
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
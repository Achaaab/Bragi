package com.github.achaaab.bragi.gui.module;

import com.github.achaaab.bragi.core.module.producer.Keyboard;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import static javax.swing.KeyStroke.getKeyStroke;

/**
 * keyboard Swing view
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.0
 */
public class KeyboardView extends JSplitPane {

	private static final String KEY_PRESSED_SUFFIX = "_pressed";
	private static final String KEY_RELEASED_SUFFIX = "_released";

	/**
	 * @param model keyboard model
	 */
	public KeyboardView(Keyboard model) {

		super(VERTICAL_SPLIT);

		var keys = model.keys();
		var keyCount = keys.size();

		var keysPanel = new JPanel();
		keysPanel.setLayout(new GridLayout(1, keyCount));

		var inputMap = getInputMap(WHEN_IN_FOCUSED_WINDOW);
		var actionMap = getActionMap();

		for (var key : keys) {

			var keyView = key.view();
			keysPanel.add(keyView);

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

		setTopComponent(keysPanel);
		setBottomComponent(model.mmlPlayer().view());
	}
}
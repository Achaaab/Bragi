package fr.guehenneux.bragi.module.view;

import fr.guehenneux.bragi.module.model.Key;
import fr.guehenneux.bragi.module.model.Keyboard;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * @author Jonathan Guéhenneux
 */
public class KeyboardView extends JPanel {

	/**
	 * @param model
	 */
	public KeyboardView(Keyboard model) {

		List<Key> keys = model.getKeys();
		int keyCount = keys.size();
		setLayout(new GridLayout(1, keyCount));

		for (Key key : keys) {

			KeyView keyView = new KeyView(key, model);
			add(keyView);
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

		JFrame frame = new JFrame(model.getName());
		frame.add(this);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
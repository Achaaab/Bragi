package fr.guehenneux.bragi.module;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * @author Jonathan Guéhenneux
 */
public class KeyboardView extends JPanel {

	private Keyboard model;

	/**
	 * @param model
	 */
	public KeyboardView(Keyboard model) {

		this.model = model;

		setLayout(new GridLayout(1, 2));

		List<Key> keys = model.getKeys();

		for (Key key : keys) {
			add(new KeyView(key, model));
		}

		JFrame frame = new JFrame(model.getName());
		frame.setSize(400, 300);
		frame.add(this);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
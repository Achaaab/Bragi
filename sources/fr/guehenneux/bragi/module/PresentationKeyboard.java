package fr.guehenneux.bragi.module;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * @author Jonathan Guéhenneux
 */
public class PresentationKeyboard extends JPanel {

	private Keyboard keyboard;

	/**
	 * @param keyboard
	 */
	public PresentationKeyboard(Keyboard keyboard) {

		this.keyboard = keyboard;

		setLayout(new GridLayout(1, 2));

		List<Key> keys = keyboard.getKeys();

		for (Key key : keys) {
			add(new PresentationKey(key, keyboard));
		}

		JFrame frame = new JFrame(keyboard.getName());
		frame.setSize(400, 300);
		frame.add(this);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
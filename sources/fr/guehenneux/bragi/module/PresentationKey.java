package fr.guehenneux.bragi.module;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author Jonathan Guéhenneux
 */
public class PresentationKey extends JButton {

	private Key key;
	private Keyboard keyboard;

	/**
	 * @param key
	 * @param keyboard
	 */
	public PresentationKey(Key key, Keyboard keyboard) {

		super(Double.toString(key.getFrequency()));

		this.key = key;
		this.keyboard = keyboard;

		addActionListener(this::press);
	}

	/**
	 * @param event
	 */
	private void press(ActionEvent event) {
		keyboard.setFrequency(key.getFrequency());
	}
}
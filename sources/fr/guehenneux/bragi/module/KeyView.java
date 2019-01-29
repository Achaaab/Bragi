package fr.guehenneux.bragi.module;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author Jonathan Guéhenneux
 */
public class KeyView extends JButton {

	private Key model;
	private Keyboard keyboard;

	/**
	 * @param model
	 * @param keyboard
	 */
	public KeyView(Key model, Keyboard keyboard) {

		super(Double.toString(model.getFrequency()));

		this.model = model;
		this.keyboard = keyboard;

		addActionListener(this::press);
	}

	/**
	 * @param event
	 */
	private void press(ActionEvent event) {
		keyboard.setFrequency(model.getFrequency());
	}
}
package fr.guehenneux.bragi.module.view;

import fr.guehenneux.bragi.module.model.Key;
import fr.guehenneux.bragi.module.model.Keyboard;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * @author Jonathan Guéhenneux
 */
public class KeyView extends JButton implements MouseListener {

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

		addMouseListener(this);
	}

	@Override
	public void mouseClicked(MouseEvent mouseEvent) {

	}

	@Override
	public void mousePressed(MouseEvent mouseEvent) {
		keyboard.press(model.getFrequency());
	}

	@Override
	public void mouseReleased(MouseEvent mouseEvent) {
		keyboard.release();
	}

	@Override
	public void mouseEntered(MouseEvent mouseEvent) {

	}

	@Override
	public void mouseExited(MouseEvent mouseEvent) {

	}
}
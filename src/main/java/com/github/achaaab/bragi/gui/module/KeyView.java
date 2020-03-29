package com.github.achaaab.bragi.gui.module;

import com.github.achaaab.bragi.module.Keyboard;
import com.github.achaaab.bragi.common.Key;

import javax.swing.JButton;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * @author Jonathan Guéhenneux
 */
public class KeyView extends JButton implements MouseListener {

	private Key model;
	private Keyboard keyboard;
	private boolean pressed;

	/**
	 * @param model
	 * @param keyboard
	 */
	public KeyView(Key model, Keyboard keyboard) {

		super(model.toString());

		this.model = model;
		this.keyboard = keyboard;

		setFocusTraversalKeysEnabled(false);
		setPreferredSize(new Dimension(65, 300));
		setBackground(model.toString().contains("#") ? Color.BLACK : Color.WHITE);
		setForeground(model.toString().contains("#") ? Color.WHITE : Color.BLACK);

		pressed = false;

		addMouseListener(this);
	}

	@Override
	public void mouseClicked(MouseEvent mouseEvent) {

	}

	@Override
	public void mousePressed(MouseEvent mouseEvent) {
		press();
	}

	@Override
	public void mouseReleased(MouseEvent mouseEvent) {
		release();
	}

	@Override
	public void mouseEntered(MouseEvent mouseEvent) {

		int modifiers = mouseEvent.getModifiersEx();

		if ((modifiers & MouseEvent.BUTTON1_DOWN_MASK) == MouseEvent.BUTTON1_DOWN_MASK) {
			press();
		}
	}

	@Override
	public void mouseExited(MouseEvent mouseEvent) {
		release();
	}

	/**
	 *
	 */
	public void press() {

		if (!pressed) {

			pressed = true;
			keyboard.press(model.getVoltage());
		}
	}

	/**
	 *
	 */
	public void release() {

		if (pressed) {

			pressed = false;
			keyboard.release();
		}
	}
}
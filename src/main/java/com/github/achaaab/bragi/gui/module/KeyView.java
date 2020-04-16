package com.github.achaaab.bragi.gui.module;

import com.github.achaaab.bragi.core.module.producer.Key;
import com.github.achaaab.bragi.core.module.producer.Keyboard;

import javax.swing.JButton;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import static com.github.achaaab.bragi.gui.common.ViewScale.scale;
import static java.awt.Color.BLACK;
import static java.awt.Color.WHITE;
import static java.awt.event.InputEvent.BUTTON1_DOWN_MASK;

/**
 * key Swing view
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.0
 */
public class KeyView extends JButton implements MouseListener {

	private static final Dimension SIZE = scale(new Dimension(40, 200));

	private final Key model;
	private final Keyboard keyboard;

	private boolean pressed;

	/**
	 * @param model    key model
	 * @param keyboard keyboard model
	 */
	public KeyView(Key model, Keyboard keyboard) {

		this.model = model;
		this.keyboard = keyboard;

		setPreferredSize(SIZE);

		if (model.note().tone() == 0) {
			setText(Integer.toString(model.note().octave()));
		}

		setBackground(model.sharp() ? BLACK : WHITE);
		setForeground(model.sharp() ? WHITE : BLACK);

		pressed = false;
		setFocusTraversalKeysEnabled(false);
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

		var modifiers = mouseEvent.getModifiersEx();

		if ((modifiers & BUTTON1_DOWN_MASK) == BUTTON1_DOWN_MASK) {
			press();
		}
	}

	@Override
	public void mouseExited(MouseEvent mouseEvent) {
		release();
	}

	/**
	 * Presses this key.
	 */
	public void press() {

		if (!pressed) {

			pressed = true;
			keyboard.press(model);
		}
	}

	/**
	 * Releases this key.
	 */
	public void release() {

		if (pressed) {

			pressed = false;
			keyboard.release();
		}
	}
}
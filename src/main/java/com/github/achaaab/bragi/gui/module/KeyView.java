package com.github.achaaab.bragi.gui.module;

import com.github.achaaab.bragi.core.module.producer.Key;

import javax.swing.JButton;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import static com.github.achaaab.bragi.gui.common.ViewScale.scale;
import static java.awt.Color.BLACK;
import static java.awt.Color.RED;
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
	private final Color background;

	/**
	 * @param model key model
	 * @since 0.2.0
	 */
	public KeyView(Key model) {

		this.model = model;

		var note = model.note();
		var tone = note.tone();
		var octave = note.octave();
		var sharp = model.sharp();

		if (tone == 0) {
			setText(Integer.toString(octave));
		}

		setPreferredSize(SIZE);
		background = sharp ? BLACK : WHITE;
		setBackground(background);

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
	 *
	 * @since 0.2.0
	 */
	public void press() {
		model.press();
	}

	/**
	 * Releases this key.
	 *
	 * @since 0.2.0
	 */
	public void release() {
		model.release();
	}

	/**
	 * @param pressed whether this key is pressed
	 * @since 0.2.0
	 */
	public void setPressed(boolean pressed) {
		setBackground(pressed ? RED : background);
	}
}

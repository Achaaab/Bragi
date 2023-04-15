package com.github.achaaab.bragi.core.module.producer;

import com.github.achaaab.bragi.common.AbstractNamedEntity;
import com.github.achaaab.bragi.core.module.ModuleCreationException;
import com.github.achaaab.bragi.gui.module.KeyView;
import com.github.achaaab.bragi.scale.Note;

import java.lang.reflect.InvocationTargetException;

import static javax.swing.SwingUtilities.invokeAndWait;

/**
 * @author Jonathan Guéhenneux
 * @since 0.0.6
 */
public class Key extends AbstractNamedEntity {

	private final Keyboard keyboard;
	private final Note note;
	private final boolean sharp;
	private final float voltage;
	private final int code;

	private boolean pressed;
	private KeyView view;

	/**
	 * Creates a new key.
	 *
	 * @param keyboard where to which associate the key to create
	 * @param note note to play when the key will be pressed
	 * @param name name of the key to create
	 * @param sharp whether the note to play is a sharp note
	 * @param voltage voltage to output when the key will be pressed
	 * @param code code associated to this key
	 * @since 0.1.8
	 */
	public Key(Keyboard keyboard, Note note, String name, boolean sharp, float voltage, int code) {

		super(name);

		this.keyboard = keyboard;
		this.note = note;
		this.sharp = sharp;
		this.voltage = voltage;
		this.code = code;

		pressed = false;

		try {
			invokeAndWait(() -> view = new KeyView(this));
		} catch (InterruptedException | InvocationTargetException cause) {
			throw new ModuleCreationException(cause);
		}
	}

	/**
	 * @return note associated to this key
	 * @since 0.2.0
	 */
	public Note note() {
		return note;
	}

	/**
	 * @return whether the note associated to this key is a sharp note
	 * @since 0.2.0
	 */
	public boolean sharp() {
		return sharp;
	}

	/**
	 * @return voltage associated to this key
	 * @since 0.2.0
	 */
	public float voltage() {
		return voltage;
	}

	/**
	 * @return key code
	 * @since 0.2.0
	 */
	public int code() {
		return code;
	}

	/**
	 * @return view of this key
	 * @since 0.2.0
	 */
	public KeyView view() {
		return view;
	}

	/**
	 * Presses this key.
	 *
	 * @since 0.2.0
	 */
	public void press() {

		if (!pressed) {
			keyboard.press(this);
		}
	}

	/**
	 * Releases this key.
	 *
	 * @since 0.2.0
	 */
	public void release() {

		if (pressed) {
			keyboard.release(this);
		}
	}

	/**
	 * @param pressed whether this key is pressed
	 * @since 0.2.0
	 */
	public void setPressed(boolean pressed) {

		this.pressed = pressed;

		view.setPressed(pressed);
	}
}

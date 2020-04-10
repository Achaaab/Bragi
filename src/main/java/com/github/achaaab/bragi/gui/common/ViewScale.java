package com.github.achaaab.bragi.gui.common;

import javax.swing.JComponent;
import javax.swing.border.TitledBorder;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;

import static java.lang.Math.round;

/**
 * @author Jonathan Guéhenneux
 * @since 0.1.8
 */
public class ViewScale {

	private static final float SCALE_FACTOR = 1.5f;

	/**
	 * Scales the given component.
	 *
	 * @param component component to scale
	 */
	public static void scale(Component component) {

		var font = component.getFont();
		var scaledFont = scale(font);
		component.setFont(scaledFont);

		if (component instanceof JComponent jComponent) {

			var border = jComponent.getBorder();

			if (border != null) {

				if (border instanceof TitledBorder titledBorder) {

					font = titledBorder.getTitleFont();
					scaledFont = scale(font);
					titledBorder.setTitleFont(scaledFont);
				}
			}
		}

		if (component instanceof Container container) {

			for (var child : container.getComponents()) {
				scale(child);
			}
		}
	}

	/**
	 * @param font font to scale
	 * @return scaled font
	 */
	public static Font scale(Font font) {

		var fontSize = font.getSize();
		var scaledFontSize = SCALE_FACTOR * fontSize;

		return font.deriveFont(scaledFontSize);
	}

	/**
	 * @param size size
	 * @return scaled size
	 */
	public static int scale(int size) {
		return round(SCALE_FACTOR * size);
	}
}
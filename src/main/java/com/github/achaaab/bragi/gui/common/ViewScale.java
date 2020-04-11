package com.github.achaaab.bragi.gui.common;

import javax.swing.JComponent;
import javax.swing.JMenu;
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

		if (font != null) {

			var scaledFont = scale(font);
			component.setFont(scaledFont);
		}

		if (component instanceof JComponent jComponent) {

			var border = jComponent.getBorder();

			if (border != null) {

				if (border instanceof TitledBorder titledBorder) {

					font = titledBorder.getTitleFont();
					var scaledFont = scale(font);
					titledBorder.setTitleFont(scaledFont);
				}
			}
		}

		if (component instanceof Container container) {

			for (var child : container.getComponents()) {
				scale(child);
			}
		}

		if (component instanceof JMenu menu) {

			for (var child : menu.getMenuComponents()) {
				scale(child);
			}
		}
	}

	/**
	 * @param font font to scale
	 * @return scaled font
	 */
	public static Font scale(Font font) {
		return scale(font, SCALE_FACTOR);
	}

	/**
	 * @param font   font to scale
	 * @param factor scale factor
	 * @return scaled font
	 */
	public static Font scale(Font font, float factor) {

		var fontSize = font.getSize();
		var scaledFontSize = factor * fontSize;

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
package com.github.achaaab.bragi.gui.common;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.border.TitledBorder;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;

import static java.awt.Toolkit.getDefaultToolkit;
import static java.lang.Math.round;

/**
 * @author Jonathan Guéhenneux
 * @since 0.1.8
 */
public class ViewScale {

	private static final int BASE_RESOLUTION = 72;
	private static final int RESOLUTION = getDefaultToolkit().getScreenResolution();

	/**
	 * @param size normalized size for 72 DPI resolution
	 * @return scaled size
	 * @since 0.0.0
	 */
	public static float scale(float size) {
		return size * RESOLUTION / BASE_RESOLUTION;
	}

	/**
	 * @param size normalized size for 72 DPI resolution
	 * @return scaled and rounded size
	 * @since 0.0.0
	 */
	public static int scaleAndRound(float size) {
		return round(scale(size));
	}

	/**
	 * Scales the given component.
	 *
	 * @param component component to scale
	 * @since 0.2.0
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
	 * @since 0.2.0
	 */
	public static Font scale(Font font) {

		var fontSize = font.getSize();
		var scaledFontSize = scale(fontSize);

		return font.deriveFont(scaledFontSize);
	}

	/**
	 * @param dimension dimension to scale, in place
	 * @return scaled dimension
	 * @since 0.2.0
	 */
	public static Dimension scale(Dimension dimension) {

		dimension.width = scaleAndRound(dimension.width);
		dimension.height = scaleAndRound(dimension.height);

		return dimension;
	}

	/**
	 * "Scales" the given point in place. Increases its distance from origin by scale factor.
	 *
	 * @param point point to scale, in place
	 * @return scaled point
	 * @since 0.2.0
	 */
	public static Point scale(Point point) {

		point.x = scaleAndRound(point.x);
		point.y = scaleAndRound(point.y);

		return point;
	}
}

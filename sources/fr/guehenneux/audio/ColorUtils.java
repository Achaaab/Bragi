package fr.guehenneux.audio;

import java.awt.Color;

/**
 * @author GUEHENNEUX
 */
public class ColorUtils {

    /**
     * @param minimum
     * @param colorMinimum
     * @param maximum
     * @param colorMaximum
     * @return
     */
    public static Color getColorValue(double minimum, Color colorMinimum, double maximum, Color colorMaximum, double value) {

        Color colorValue;

        if (minimum > maximum) {

            colorValue = Color.BLACK;

        } else if (value < minimum) {

            colorValue = colorMinimum;

        } else if (value > maximum) {

            colorValue = colorMaximum;

        } else {

            double range = maximum - minimum;
            double relativeValue = value - minimum;
            double colorMaximumPart = relativeValue / range;
            double colorMinimumPart = 1.0 - colorMaximumPart;

            double r = colorMinimumPart * colorMinimum.getRed() + colorMaximumPart
                * colorMaximum.getRed();

            double g = colorMinimumPart * colorMinimum.getGreen() + colorMaximumPart
                * colorMaximum.getGreen();

            double b = colorMinimumPart * colorMinimum.getBlue() + colorMaximumPart
                * colorMaximum.getBlue();

            int red = (int) Math.round(r);
            int green = (int) Math.round(g);
            int blue = (int) Math.round(b);

            colorValue = new Color(red, green, blue);

        }

        return colorValue;
    }
}
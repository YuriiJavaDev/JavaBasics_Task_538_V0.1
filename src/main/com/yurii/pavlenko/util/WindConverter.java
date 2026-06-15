package main.com.yurii.pavlenko.util;

/**
 * Utility class for wind metrics conversion and direction mapping.
 */
public class WindConverter {

    public static double convertKmToMetersPerSecond(double speedKmH) {
        double speedMs = speedKmH / 3.6;
        return Math.round(speedMs * 10.0) / 10.0;
    }

    public static String getWindDirectionArrow(int degrees) {
        if (degrees < 0 || degrees > 360) {
            return "💨";
        }

        String[] arrows = {"↑ N", "↗ NE", "→ E", "↘ SE", "↓ S", "↙ SW", "← W", "↖ NW"};
        int index = (int) Math.round(((degrees % 360) / 45.0)) % 8;
        return arrows[index];
    }
}
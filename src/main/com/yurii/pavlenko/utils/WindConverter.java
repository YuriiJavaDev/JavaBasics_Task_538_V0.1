package main.com.yurii.pavlenko.utils;

/**
 * Utility class for wind metrics conversion and direction mapping.
 */
public class WindConverter {

    public static double convertKmToMetersPerSecond(double speedKmH) {
        double speedMs = speedKmH / 3.6;
        return Math.round(speedMs * 10.0) / 10.0;
    }

    /**
     * Returns a wind emoji for general status tracking.
     */
    public static String getWindEmoji() {
        return "💨";
    }

    /**
     * Returns direction arrow combined with compass heading literal (e.g. "↑ N", "↘ SE").
     */
    public static String getWindDirectionArrow(int degrees) {
        if (degrees < 0 || degrees > 360) {
            return "N/A";
        }

        String[] arrows = {"↑ N", "↗ NE", "→ E", "↘ SE", "↓ S", "↙ SW", "← W", "↖ NW"};
        int index = (int) Math.round(((degrees % 360) / 45.0)) % 8;
        return arrows[index];
    }
}
package com.yurii.pavlenko.utils;

/**
 * Utility class for wind metrics conversion and direction mapping.
 */
public class WindConverter {

    public static double convertKmToMetersPerSecond(double speedKmH) {
        double speedMs = speedKmH / 3.6;
        return Math.round(speedMs * 10.0) / 10.0;
    }

    /**
     * Formats the wind data into a user-friendly display string.
     */
    public static String formatWindDisplay(double speedMs, int degrees) {
        String arrow = getWindDirectionArrow(degrees);
        return String.format("Wind Speed: %.1f m/s (%d° %s)", speedMs, degrees, arrow);
    }

    public static String getWindEmoji() {
        return "💨";
    }

    public static String getWindDirectionArrow(int degrees) {
        if (degrees < 0 || degrees > 360) {
            return "N/A";
        }

        String[] arrows = {"↑ N", "↗ NE", "→ E", "↘ SE", "↓ S", "↙ SW", "← W", "↖ NW"};
        int index = (int) Math.round(((degrees % 360) / 45.0)) % 8;
        return arrows[index];
    }
}
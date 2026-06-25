package com.yurii.pavlenko.utils;

/**
 * Utility class for wind metrics conversion and direction mapping.
 */
public class WindConverter {

    public static double convertKmToMetersPerSecond(double speedKmH) {
        double speedMs = speedKmH / 3.6;
        return Math.round(speedMs * 10.0) / 10.0;
    }

    public static String getCompassDirectionName(double degrees) {
        String[] directions = {"N", "NE", "E", "SE", "S", "SW", "W", "NW"};
        int index = (int) Math.round(((degrees % 360) / 45.0)) % 8;
        return directions[index];
    }
}
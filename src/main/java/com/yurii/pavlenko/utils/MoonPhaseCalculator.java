package com.yurii.pavlenko.utils;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Astronomical utility calculating current moon phases and illumination percentages based on synodic lunar month cycles.
 */
public class MoonPhaseCalculator {

    private static final double LUNAR_MONTH_DAYS = 29.530588853;
    private static final LocalDate BASE_NEW_MOON = LocalDate.of(2000, 1, 6);

    public static String getMoonPhase(LocalDate date) {
        double currentAge = getMoonAge(date);

        String percentageStr = getMoonIlluminationPercentage(currentAge);

        if (currentAge < 1.845)   return "🌑 Moon Phase: New Moon " + percentageStr;
        if (currentAge < 5.536)   return "🌒 Moon Phase: Waxing Crescent " + percentageStr;
        if (currentAge < 9.228)   return "🌓 Moon Phase: First Quarter " + percentageStr;
        if (currentAge < 12.919)  return "🌔 Moon Phase: Waxing Gibbous " + percentageStr;
        if (currentAge < 16.610)  return "🌕 Moon Phase: Full Moon " + percentageStr;
        if (currentAge < 20.302)  return "🌖 Moon Phase: Waning Gibbous " + percentageStr;
        if (currentAge < 23.993)  return "🌗 Moon Phase: Last Quarter " + percentageStr;
        if (currentAge < 27.684)  return "🌘 Moon Phase: Waning Crescent " + percentageStr;

        return "🌑 Moon Phase: New Moon " + percentageStr;
    }

    /**
     * Calculates the calculated illumination percentage formatted as a string.
     */
    public static String getMoonIlluminationPercentage(double currentAge) {
        double halfCycle = LUNAR_MONTH_DAYS / 2.0;
        double illumination;

        if (currentAge <= halfCycle) {
            illumination = (currentAge / halfCycle) * 100.0;
        } else {
            illumination = ((LUNAR_MONTH_DAYS - currentAge) / halfCycle) * 100.0;
        }

        return String.format("(%.0f%%)", illumination);
    }

    private static double getMoonAge(LocalDate date) {
        long daysSinceBase = ChronoUnit.DAYS.between(BASE_NEW_MOON, date);
        double age = daysSinceBase % LUNAR_MONTH_DAYS;
        if (age < 0) {
            age += LUNAR_MONTH_DAYS;
        }
        return age;
    }
}
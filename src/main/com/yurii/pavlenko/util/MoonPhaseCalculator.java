package main.com.yurii.pavlenko.util;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Astronomical utility calculating current moon phases based on synodic lunar month cycles.
 */
public class MoonPhaseCalculator {

    private static final double LUNAR_MONTH_DAYS = 29.530588853;

    // Known base New Moon date (January 6, 2000)
    private static final LocalDate BASE_NEW_MOON = LocalDate.of(2000, 1, 6);

    public static String getMoonPhase(LocalDate date) {
        long daysSinceBase = ChronoUnit.DAYS.between(BASE_NEW_MOON, date);

        double currentAge = daysSinceBase % LUNAR_MONTH_DAYS;
        if (currentAge < 0) {
            currentAge += LUNAR_MONTH_DAYS;
        }

        if (currentAge < 1.845)   return "🌑 New Moon";
        if (currentAge < 5.536)   return "🌒 Waxing Crescent";
        if (currentAge < 9.228)   return "🌓 First Quarter";
        if (currentAge < 12.919)  return "🌔 Waxing Gibbous";
        if (currentAge < 16.610)  return "🌕 Full Moon";
        if (currentAge < 20.302)  return "🌖 Waning Gibbous";
        if (currentAge < 23.993)  return "🌗 Last Quarter";
        if (currentAge < 27.684)  return "🌘 Waning Crescent";

        return "🌑 New Moon";
    }
}
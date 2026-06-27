package com.yurii.pavlenko.utils;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class MoonPhaseCalculatorTest {

    @Test
    void testGetMoonPhase_NewMoon() {
        LocalDate date = LocalDate.of(2000, 1, 6);
        String phase = MoonPhaseCalculator.getMoonPhase(date);
        assertTrue(phase.contains("New Moon"));
    }

    @Test
    void testGetMoonPhase_FullMoon() {
        LocalDate date = LocalDate.of(2000, 1, 21);
        String phase = MoonPhaseCalculator.getMoonPhase(date);
        assertTrue(phase.contains("Full Moon"));
    }

    @Test
    void testGetMoonIlluminationPercentage() {
        // Age 0: New Moon, should be (0%)
        assertEquals("(0%)", MoonPhaseCalculator.getMoonIlluminationPercentage(0.0));

        // Age 7.38 (approx 1/4 of month): should be roughly 50%
        assertEquals("(50%)", MoonPhaseCalculator.getMoonIlluminationPercentage(7.3826));

        // Age 14.76 (half of month): Full moon, should be (100%)
        assertEquals("(100%)", MoonPhaseCalculator.getMoonIlluminationPercentage(14.765));
    }
}
package com.yurii.pavlenko.controller.tools.calculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ResultFormatterTest {

    private ResultFormatter formatter;

    @BeforeEach
    void setUp() {
        formatter = new ResultFormatter();
    }

    @Test
    void shouldFormatWholeNumbersAsLong() {
        assertEquals("5", formatter.formatResult(5.0));
        assertEquals("-10", formatter.formatResult(-10.0));
    }

    @Test
    void shouldFormatDecimalNumbers() {
        assertEquals("5.123", formatter.formatResult(5.123));
    }

    @Test
    void shouldHandleRoundingPrecision() {
        // Перевіряємо, що після 12 знаків округлює коректно
        assertEquals("1.123456789012", formatter.formatResult(1.123456789012345));
    }

    @Test
    void shouldReturnScientificOrRawForExtremeValues() {
        // Перевіряємо граничні умови, де код робить String.valueOf(value)
        assertEquals("1.0E15", formatter.formatResult(1e15));
        assertEquals("1.0E-13", formatter.formatResult(1e-13));
    }

    @Test
    void shouldHandleSpecialDoubleValues() {
        assertEquals("NaN", formatter.formatResult(Double.NaN));
        assertEquals("Infinity", formatter.formatResult(Double.POSITIVE_INFINITY));
    }
}
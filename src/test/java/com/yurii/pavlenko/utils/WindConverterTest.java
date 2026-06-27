package com.yurii.pavlenko.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class WindConverterTest {

    @Test
    void testConvertKmToMetersPerSecond() {
        assertEquals(0.0, WindConverter.convertKmToMetersPerSecond(0.0));
        assertEquals(2.8, WindConverter.convertKmToMetersPerSecond(10.0));
        assertEquals(10.0, WindConverter.convertKmToMetersPerSecond(36.0));
    }

    @Test
    void testGetCompassDirectionName() {
        assertEquals("N", WindConverter.getCompassDirectionName(0));
        assertEquals("N", WindConverter.getCompassDirectionName(360));
        assertEquals("NE", WindConverter.getCompassDirectionName(45));
        assertEquals("E", WindConverter.getCompassDirectionName(90));
        assertEquals("S", WindConverter.getCompassDirectionName(180));
        assertEquals("W", WindConverter.getCompassDirectionName(270));
    }
}
package com.yurii.pavlenko.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class WeatherCodeMapperTest {

    @Test
    void testGetTextForVariousCodes() {
        assertEquals("Clear Sky", WeatherCodeMapper.getText(0));
        assertEquals("Partly Cloudy", WeatherCodeMapper.getText(1));
        assertEquals("Partly Cloudy", WeatherCodeMapper.getText(2));
        assertEquals("Partly Cloudy", WeatherCodeMapper.getText(3));
        assertEquals("Foggy", WeatherCodeMapper.getText(45));
        assertEquals("Foggy", WeatherCodeMapper.getText(48));
        assertEquals("Drizzle", WeatherCodeMapper.getText(51));
        assertEquals("Rainy", WeatherCodeMapper.getText(61));
        assertEquals("Snowy", WeatherCodeMapper.getText(71));
        assertEquals("Rain Showers", WeatherCodeMapper.getText(80));
        assertEquals("Thunderstorm", WeatherCodeMapper.getText(95));
        assertEquals("Cloudy", WeatherCodeMapper.getText(999));
    }
}
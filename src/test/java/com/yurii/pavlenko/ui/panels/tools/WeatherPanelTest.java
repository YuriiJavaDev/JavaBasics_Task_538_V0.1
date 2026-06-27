package com.yurii.pavlenko.ui.panels.tools;

import com.yurii.pavlenko.model.tools.weather.WeatherModelDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WeatherPanelTest {

    private WeatherPanel panel;

    @BeforeEach
    void setUp() {
        panel = new WeatherPanel();
    }

    @Test
    void testUpdateWeatherDisplay_UpdatesLabelsCorrectly() {
        WeatherModelDTO data = new WeatherModelDTO();
        data.setCityName("Rishon LeZion");
        data.setCountryCode("IL");
        data.setTemperature(25.0);
        data.setHumidity(60);
        data.setWindSpeed(5.0);
        data.setWindDirection(180);
        data.setWeatherCode(0);

        panel.updateWeatherDisplay(data);

        // Verification using getters
        assertTrue(panel.getLocationText().contains("Rishon LeZion"));
        assertTrue(panel.getLocationText().contains("IL"));
        assertNotNull(panel.getConditionText());
        assertTrue(panel.getTempText().contains("25"));
    }
}
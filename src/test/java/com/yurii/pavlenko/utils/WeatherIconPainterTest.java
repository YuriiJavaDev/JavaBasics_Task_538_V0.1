package com.yurii.pavlenko.utils;

import org.junit.jupiter.api.Test;
import javax.swing.ImageIcon;
import java.awt.image.BufferedImage;
import static org.junit.jupiter.api.Assertions.*;

class WeatherIconPainterTest {

    @Test
    void testCreateIconReturnsValidImage() {
        int[] codes = {0, 1, 2, 51, 61, 71, 95, 999};
        for (int code : codes) {
            BufferedImage img = WeatherIconPainter.createIcon(code);
            assertNotNull(img);
            assertEquals(80, img.getWidth());
            assertEquals(60, img.getHeight());
        }
    }

    @Test
    void testCreateMetricIconsReturnValidImageIcon() {
        assertNotNull(WeatherIconPainter.createThermometerIcon());
        assertNotNull(WeatherIconPainter.createWindIcon());
        assertNotNull(WeatherIconPainter.createHumidityIcon());
    }

    @Test
    void testCreateCompassIconReturnsValidImageIcon() {
        ImageIcon icon = WeatherIconPainter.createCompassIcon(90);
        assertNotNull(icon);
        assertEquals(32, icon.getIconWidth());
        assertEquals(32, icon.getIconHeight());
    }
}
package main.com.yurii.pavlenko.ui.panels.tools;

import javax.swing.*;
import java.awt.*;

/**
 * Visual viewport component presenting meteorological analytics and forecasting.
 */
public class WeatherPanel extends JPanel {
    public WeatherPanel() {
        setBorder(BorderFactory.createTitledBorder("Local Weather Forecast"));
        setLayout(new BorderLayout());

        // Placeholder label for future logic
        JLabel placeholder = new JLabel("Weather Dashboard Placeholder", SwingConstants.CENTER);
        add(placeholder, BorderLayout.CENTER);
    }
}
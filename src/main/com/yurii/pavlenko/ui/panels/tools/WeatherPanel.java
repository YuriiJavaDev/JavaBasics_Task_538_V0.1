package main.com.yurii.pavlenko.ui.panels.tools;

import main.com.yurii.pavlenko.controller.tools.weather.WeatherController;
import main.com.yurii.pavlenko.model.tools.weather.WeatherModelDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Visual viewport component presenting meteorological analytics and forecasting.
 */
public class WeatherPanel extends JPanel {

    private JTextField txtLatitude;
    private JTextField txtLongitude;
    private JButton btnFetch;

    private JPanel cardPanel;
    private JLabel lblStatus;
    private JLabel lblTemperature;
    private JLabel lblWind;
    private JLabel lblHumidity;
    private JLabel lblMoon;

    public WeatherPanel() {
        setBorder(BorderFactory.createTitledBorder("Local Weather Forecast"));
        setLayout(new BorderLayout(5, 5));

        initInputBar();
        initWeatherCard();
    }

    private void initInputBar() {
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));

        JLabel lblLat = new JLabel("Lat:");
        lblLat.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtLatitude = new JTextField("53.9", 4);

        JLabel lblLon = new JLabel("Lon:");
        lblLon.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtLongitude = new JTextField("27.56", 4);

        btnFetch = new JButton("Find");
        btnFetch.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnFetch.setFocusPainted(false);

        inputPanel.add(lblLat);
        inputPanel.add(txtLatitude);
        inputPanel.add(lblLon);
        inputPanel.add(txtLongitude);
        inputPanel.add(btnFetch);

        add(inputPanel, BorderLayout.NORTH);
    }

    private void initWeatherCard() {
        cardPanel = new JPanel(new GridBagLayout());
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(10, 10, 10, 10),
                BorderFactory.createLineBorder(new Color(210, 210, 214), 1, true)
        ));
        cardPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.insets = new Insets(6, 15, 6, 15);
        gbc.anchor = GridBagConstraints.CENTER;

        lblStatus = new JLabel("☀️ Loading...");
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 20));

        lblTemperature = new JLabel("-- °C");
        lblTemperature.setFont(new Font("Segoe UI", Font.PLAIN, 18));

        lblWind = new JLabel("💨 Wind: -- m/s");
        lblWind.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        lblHumidity = new JLabel("💧 Humidity: -- %");
        lblHumidity.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        lblMoon = new JLabel("🌑 Moon Phase: --");
        lblMoon.setFont(new Font("Segoe UI", Font.ITALIC, 12));

        cardPanel.add(lblStatus, gbc);
        cardPanel.add(lblTemperature, gbc);
        cardPanel.add(lblWind, gbc);
        cardPanel.add(lblHumidity, gbc);
        cardPanel.add(lblMoon, gbc);

        add(cardPanel, BorderLayout.CENTER);
    }

    public void updateWeatherDisplay(WeatherModelDTO data) {
        lblStatus.setText(getWeatherEmoji(data.getWeatherCode()) + " " + getWeatherDesc(data.getWeatherCode()));
        lblTemperature.setText(data.getTemperature() + " °C");
        lblWind.setText("💨 Wind: " + data.getWindSpeedMs() + " m/s " + data.getWindDirection());
        lblHumidity.setText("💧 Humidity: " + data.getHumidity() + " %");
        lblMoon.setText(data.getMoonPhase());

        if (data.getTemperature() <= 0) {
            cardPanel.setBackground(new Color(225, 243, 254));
        } else if (data.getTemperature() < 22) {
            cardPanel.setBackground(new Color(241, 254, 245));
        } else {
            cardPanel.setBackground(new Color(255, 248, 238));
        }
    }

    public void displayError(String message) {
        lblStatus.setText("❌ Error");
        lblTemperature.setText("");
        lblWind.setText(message);
        lblHumidity.setText("");
        lblMoon.setText("");
        cardPanel.setBackground(new Color(255, 243, 243));
    }

    public void registerController(WeatherController controller) {
        btnFetch.addActionListener(controller);
    }

    public String getLatitudeInput() { return txtLatitude.getText().trim(); }
    public String getLongitudeInput() { return txtLongitude.getText().trim(); }
    public void setButtonsEnabled(boolean enabled) { btnFetch.setEnabled(enabled); }

    private String getWeatherEmoji(int code) {
        if (code == 0) return "☀️";
        if (code <= 3) return "⛅";
        if (code <= 48) return "🌫️";
        if (code <= 67) return "🌧️";
        if (code <= 77) return "❄️";
        if (code <= 82) return "🌧️";
        if (code <= 86) return "❄️";
        return "⛈️";
    }

    private String getWeatherDesc(int code) {
        if (code == 0) return "Clear Sky";
        if (code <= 3) return "Mainly Clear";
        if (code <= 48) return "Foggy";
        if (code <= 67) return "Rain";
        if (code <= 77) return "Snow fall";
        if (code <= 82) return "Rain showers";
        if (code <= 86) return "Snow showers";
        return "Thunderstorm";
    }
}
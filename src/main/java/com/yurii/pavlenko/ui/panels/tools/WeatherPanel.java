package com.yurii.pavlenko.ui.panels.tools;

import com.yurii.pavlenko.model.tools.weather.WeatherModelDTO;
import com.yurii.pavlenko.service.tools.weather.impl.WeatherServiceImpl;
import com.yurii.pavlenko.utils.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;

/**
 * WeatherPanel component for displaying atmospheric data.
 * Refactored to delegate icon generation to WeatherIconPainter.
 */
public class WeatherPanel extends JPanel {

    private JTextField txtCity;
    private JButton btnSearch;
    private JPanel contentCard;
    private JLabel lblLocation;
    private JLabel lblWeatherIcon;
    private JLabel lblCondition;
    private JLabel lblTemp;
    private JLabel lblWind;
    private JLabel lblHumidity;
    private JLabel lblMoonText;

    public WeatherPanel() {
        setBorder(BorderFactory.createTitledBorder("Local Weather Forecast"));
        setLayout(new BorderLayout(8, 8));

        initTopBar();
        initContentCard();
    }

    private void initTopBar() {
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        Font mainFont = new Font("Segoe UI", Font.PLAIN, 12);

        txtCity = new JTextField("Rishon LeZion", 12);
        txtCity.setFont(mainFont);

        btnSearch = new JButton("Search");
        btnSearch.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnSearch.setFocusPainted(false);
        btnSearch.setFocusable(false);

        txtCity.addActionListener(e -> { if (btnSearch.isEnabled()) btnSearch.doClick(120); });
        btnSearch.addActionListener(e -> txtCity.requestFocusInWindow());

        topBar.add(new JLabel("City:"));
        topBar.add(txtCity);
        topBar.add(btnSearch);

        add(topBar, BorderLayout.NORTH);
    }

    private void initContentCard() {
        contentCard = new JPanel(new GridBagLayout());
        contentCard.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(10, 15, 10, 15),
                BorderFactory.createLineBorder(new Color(220, 224, 230), 1, true)
        ));
        contentCard.setBackground(new Color(250, 252, 255));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.anchor = GridBagConstraints.CENTER;

        lblLocation = new JLabel("Enter a city to see the weather");
        lblLocation.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblLocation.setForeground(new Color(60, 70, 85));

        gbc.insets = new Insets(12, 0, 8, 0);
        contentCard.add(lblLocation, gbc);

        // Initial empty icon
        lblWeatherIcon = new JLabel(new ImageIcon(WeatherIconPainter.createIcon(0)));
        contentCard.add(lblWeatherIcon, gbc);

        lblCondition = new JLabel("-");
        lblCondition.setFont(new Font("Segoe UI", Font.BOLD, 16));
        contentCard.add(lblCondition, gbc);

        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(6, 30, 6, 30);

        lblTemp = createMetricLabel("", WeatherIconPainter.createThermometerIcon());
        contentCard.add(lblTemp, gbc);

        lblWind = createMetricLabel("", WeatherIconPainter.createWindIcon());
        contentCard.add(lblWind, gbc);

        lblHumidity = createMetricLabel("", WeatherIconPainter.createHumidityIcon());
        contentCard.add(lblHumidity, gbc);

        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(18, 0, 25, 0);
        lblMoonText = new JLabel();
        lblMoonText.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        lblMoonText.setPreferredSize(new Dimension(360, 40));
        contentCard.add(lblMoonText, gbc);

        add(contentCard, BorderLayout.CENTER);
    }

    private JLabel createMetricLabel(String text, ImageIcon icon) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        label.setIcon(icon);
        label.setIconTextGap(12);
        return label;
    }

    public void updateWeatherDisplay(WeatherModelDTO data) {
        String englishCity = CityTranslitUtil.convertToEnglishText(data.getCityName());
        String directionName = WindConverter.getCompassDirectionName(data.getWindDirection());

        lblLocation.setText("Location: " + englishCity + ", " + data.getCountryCode());
        lblCondition.setText(WeatherServiceImpl.mapCodeToText(data.getWeatherCode()));
        lblTemp.setText(String.format("Temperature: %.0f °C", data.getTemperature()));
        lblWind.setIcon(WeatherIconPainter.createCompassIcon(data.getWindDirection()));
        lblWind.setText(String.format("Wind: %.1f m/s (%d° %s)", data.getWindSpeed(), data.getWindDirection(), directionName));
        lblHumidity.setText(String.format("Humidity: %d %%", data.getHumidity()));
        lblWeatherIcon.setIcon(new ImageIcon(WeatherIconPainter.createIcon(data.getWeatherCode())));
        updateMoonPhaseDisplay();
    }

    public void updateMoonPhaseDisplay() {
        lblMoonText.setText(MoonPhaseCalculator.getMoonPhase(LocalDate.now()));
    }

    public void displayError(String message) {
        lblCondition.setText("Error");
        lblTemp.setText(message);
        lblWind.setText("");
        lblHumidity.setText("");
    }

    public String getCityInput() { return txtCity.getText().trim(); }
    public void setButtonsEnabled(boolean enabled) { btnSearch.setEnabled(enabled); }
    public void registerController(java.awt.event.ActionListener controller) { btnSearch.addActionListener(controller); }
}
package com.yurii.pavlenko.ui.panels.tools;

import com.yurii.pavlenko.model.tools.weather.WeatherModelDTO;
import com.yurii.pavlenko.utils.CityTranslitUtil;
import com.yurii.pavlenko.utils.MoonPhaseCalculator;
import com.yurii.pavlenko.utils.WindConverter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.time.LocalDate;

/**
 * Visual panel component for tracking atmospheric conditions and astronomical lunar cycles.
 * Uses specialized utilities for calculations and text translations.
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

        // Блокируем кнопке возможность воровать фокус у текстового поля при клике мышкой
        btnSearch.setFocusable(false);

        // Дублируем нажатие Enter в поле города на кнопку Search с визуальным откликом
        txtCity.addActionListener(e -> {
            if (btnSearch.isEnabled()) {
                btnSearch.doClick(120);
            }
        });

        // При нажатии мышкой на кнопку — возврат фокуса в текстовое поле города!
        btnSearch.addActionListener(e -> {
            txtCity.requestFocusInWindow();
        });

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

        lblLocation = new JLabel("Location: Rishon LeZion, IL");
        lblLocation.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblLocation.setForeground(new Color(60, 70, 85));
        gbc.insets = new Insets(12, 0, 8, 0);
        contentCard.add(lblLocation, gbc);

        gbc.insets = new Insets(4, 0, 4, 0);

        lblWeatherIcon = new JLabel(createCloudVectorPlaceholder());
        contentCard.add(lblWeatherIcon, gbc);

        lblCondition = new JLabel("Partly Cloudy");
        lblCondition.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblCondition.setForeground(Color.BLACK);
        gbc.insets = new Insets(4, 0, 10, 0);
        contentCard.add(lblCondition, gbc);

        // Настройка выравнивания элементов по левому краю для погодных метрик
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(6, 30, 6, 30);

        lblTemp = new JLabel("Temperature: 23 °C");
        lblTemp.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        lblTemp.setIcon(createThermometerIcon());
        lblTemp.setIconTextGap(12);
        contentCard.add(lblTemp, gbc);

        lblWind = new JLabel("Wind Speed: 1.0 m/s (151° ^ N)");
        lblWind.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        lblWind.setIcon(createWindIcon());
        lblWind.setIconTextGap(12);
        contentCard.add(lblWind, gbc);

        lblHumidity = new JLabel("Humidity: 83 %");
        lblHumidity.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        lblHumidity.setIcon(createHumidityIcon());
        lblHumidity.setIconTextGap(12);
        contentCard.add(lblHumidity, gbc);

        // Возврат выравнивания по центру для красивой строки луны из утилиты
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(18, 0, 14, 0);

        lblMoonText = new JLabel();
        lblMoonText.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        lblMoonText.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
        contentCard.add(lblMoonText, gbc);

        add(contentCard, BorderLayout.CENTER);

        updateMoonPhaseDisplay();
    }

    /**
     * Re-evaluates current moon status directly through the centralized MoonPhaseCalculator utility.
     */
    public void updateMoonPhaseDisplay() {
        LocalDate now = LocalDate.now();
        String fullMoonPhaseStr = MoonPhaseCalculator.getMoonPhase(now);
        lblMoonText.setText(fullMoonPhaseStr);
    }

    public void updateWeatherDisplay(WeatherModelDTO data) {
        String englishCity = CityTranslitUtil.convertToEnglishText(data.getCityName());
        lblLocation.setText("Location: " + englishCity + ", " + data.getCountryCode());
        lblCondition.setText(getConditionTextByCode(data.getWeatherCode()));

        lblTemp.setText(String.format("Temperature: %.0f °C", data.getTemperature()));

        String arrowDirection = WindConverter.getWindDirectionArrow(data.getWindDirection());
        lblWind.setText(String.format("Wind Speed: %.1f m/s (%d° %s)",
                data.getWindSpeed(), data.getWindDirection(), arrowDirection));

        lblHumidity.setText("Humidity: " + data.getHumidity() + " %");
        contentCard.setBackground(new Color(250, 252, 255));

        updateMoonPhaseDisplay();
    }

    public void displayError(String message) {
        lblCondition.setText("Error");
        lblTemp.setText(message);
        lblWind.setText("");
        lblHumidity.setText("");
        contentCard.setBackground(new Color(255, 243, 243));
    }

    private String getConditionTextByCode(int code) {
        return switch (code) {
            case 0 -> "Clear Sky";
            case 1, 2, 3 -> "Partly Cloudy";
            case 45, 48 -> "Foggy";
            case 51, 53, 55 -> "Drizzle";
            case 61, 63, 65 -> "Rainy";
            case 71, 73, 75 -> "Snowy";
            case 80, 81, 82 -> "Rain Showers";
            case 95, 96, 99 -> "Thunderstorm";
            default -> "Cloudy";
        };
    }

    private ImageIcon createThermometerIcon() {
        BufferedImage img = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(230, 80, 80));
        g2.fillOval(10, 20, 12, 12);
        g2.fillRect(14, 4, 4, 18);
        g2.dispose();
        return new ImageIcon(img);
    }

    private ImageIcon createWindIcon() {
        BufferedImage img = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(110, 140, 160));
        g2.setStroke(new BasicStroke(3.0f));
        g2.draw(new Line2D.Double(4, 10, 22, 10));
        g2.draw(new Line2D.Double(8, 18, 28, 18));
        g2.draw(new Line2D.Double(6, 26, 18, 26));
        g2.dispose();
        return new ImageIcon(img);
    }

    private ImageIcon createHumidityIcon() {
        BufferedImage img = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(65, 130, 215));
        g2.fillOval(8, 14, 16, 16);
        int[] xPoints = {8, 16, 24};
        int[] yPoints = {20, 4, 20};
        g2.fillPolygon(xPoints, yPoints, 3);
        g2.dispose();
        return new ImageIcon(img);
    }

    private ImageIcon createCloudVectorPlaceholder() {
        BufferedImage img = new BufferedImage(80, 50, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(160, 185, 215));
        g2.fillOval(10, 15, 35, 30);
        g2.fillOval(30, 10, 40, 35);
        g2.dispose();
        return new ImageIcon(img);
    }

    public String getCityInput() {
        return txtCity.getText().trim();
    }

    public void setButtonsEnabled(boolean enabled) {
        btnSearch.setEnabled(enabled);
    }

    public void registerController(java.awt.event.ActionListener controller) {
        btnSearch.addActionListener(controller);
    }
}
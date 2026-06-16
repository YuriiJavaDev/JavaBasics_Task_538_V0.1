package main.com.yurii.pavlenko.ui.panels.tools;

import main.com.yurii.pavlenko.controller.tools.weather.WeatherController;
import main.com.yurii.pavlenko.model.tools.weather.WeatherModelDTO;
import main.com.yurii.pavlenko.service.tools.weather.impl.WeatherServiceImpl;
import main.com.yurii.pavlenko.util.MoonPhaseCalculator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.time.LocalDate;

/**
 * Visual viewport component presenting meteorological analytics with custom drawn rich vector graphics.
 * Bypasses operating system monochrome font bugs by drawing shapes dynamically.
 */
public class WeatherPanel extends JPanel {

    private JTextField txtCity;
    private JButton btnFetch;

    private JPanel cardPanel;
    private JLabel lblIcon;
    private JLabel lblStatus;
    private JLabel lblTemperature;
    private JLabel lblWind;
    private JLabel lblHumidity;
    private JLabel lblCity;
    private JLabel lblMoonIcon;

    public WeatherPanel() {
        setBorder(BorderFactory.createTitledBorder("Local Weather Forecast"));
        setLayout(new BorderLayout(5, 5));

        initInputBar();
        initWeatherCard();
    }

    private void initInputBar() {
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));

        JLabel lblCityPre = new JLabel("City:");
        lblCityPre.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtCity = new JTextField("Rishon LeZion", 12);

        btnFetch = new JButton("Search");
        btnFetch.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnFetch.setFocusPainted(false);

        inputPanel.add(lblCityPre);
        inputPanel.add(txtCity);
        inputPanel.add(btnFetch);

        add(inputPanel, BorderLayout.NORTH);
    }

    private void initWeatherCard() {
        cardPanel = new JPanel(new GridBagLayout());
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(15, 15, 15, 15),
                BorderFactory.createLineBorder(new Color(210, 215, 225), 1, true)
        ));
        cardPanel.setBackground(new Color(250, 250, 253));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.anchor = GridBagConstraints.WEST;

        Font mainFont = new Font("Segoe UI", Font.PLAIN, 14);

        lblIcon = new JLabel();
        lblStatus = new JLabel("Clear Sky");
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 18));

        lblTemperature = new JLabel("Temperature: -- °C");
        lblTemperature.setFont(mainFont);
        lblTemperature.setIcon(createParamIcon("temp", 0));

        lblWind = new JLabel("Wind Speed: -- m/s");
        lblWind.setFont(mainFont);
        lblWind.setIcon(createParamIcon("wind", 0));

        lblHumidity = new JLabel("Humidity: -- %");
        lblHumidity.setFont(mainFont);
        lblHumidity.setIcon(createParamIcon("humidity", 0));

        lblCity = new JLabel("Location: --");
        lblCity.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblCity.setForeground(new Color(80, 80, 80));

        lblMoonIcon = new JLabel();
        lblMoonIcon.setFont(mainFont);

        GridBagConstraints gbcCenter = new GridBagConstraints();
        gbcCenter.gridx = 0;
        gbcCenter.gridy = GridBagConstraints.RELATIVE;
        gbcCenter.insets = new Insets(6, 10, 6, 10);
        gbcCenter.anchor = GridBagConstraints.CENTER;

        cardPanel.add(lblIcon, gbcCenter);
        cardPanel.add(lblStatus, gbcCenter);
        cardPanel.add(lblTemperature, gbc);
        cardPanel.add(lblWind, gbc);
        cardPanel.add(lblHumidity, gbc);
        cardPanel.add(lblCity, gbc);
        cardPanel.add(lblMoonIcon, gbc);

        add(cardPanel, BorderLayout.CENTER);
    }

    public void updateWeatherDisplay(WeatherModelDTO data) {
        lblIcon.setIcon(createWeatherIcon(data.getWeatherCode()));
        lblStatus.setText(WeatherServiceImpl.codeToTextStatus(data.getWeatherCode()));
        lblTemperature.setText("Temperature: " + data.getTemperature() + " °C");
        lblHumidity.setText("Humidity: " + data.getHumidity() + " %");
        lblCity.setText("Location: " + data.getCityName() + ", " + data.getCountryCode().toUpperCase());

        String directionText = WeatherServiceImpl.degreesToDirection(data.getWindDirection());
        lblWind.setText("Wind Speed: " + String.format("%.1f", data.getWindSpeed()) + " m/s " + directionText);
        lblWind.setIcon(createParamIcon("wind", data.getWindDirection()));

        // Луна: Срезаем из строки калькулятора любые юникодные квадратики и спецсимволы
        String rawPhaseText = MoonPhaseCalculator.getMoonPhase(LocalDate.now());
        String cleanPhaseText = rawPhaseText.replaceAll("[^a-zA-Z0-9 ]", "").trim();
        String percentageText = getPhasePercentage(cleanPhaseText);

        lblMoonIcon.setIcon(createMoonPhaseIcon(cleanPhaseText));
        lblMoonIcon.setText("Moon Phase: " + percentageText + " " + cleanPhaseText);

        cardPanel.setBackground(new Color(245, 247, 250));
    }

    public void displayError(String message) {
        lblIcon.setIcon(createWeatherIcon(-1));
        lblStatus.setText("Error occurred");
        lblTemperature.setText(message);
        lblWind.setText("");
        lblWind.setIcon(null);
        lblHumidity.setText("");
        lblCity.setText("");
        lblMoonIcon.setIcon(null);
        lblMoonIcon.setText("");
        cardPanel.setBackground(new Color(255, 245, 245));
    }

    private String getPhasePercentage(String phase) {
        if (phase == null) return "0%";
        if (phase.contains("New Moon")) return "0%";
        if (phase.contains("Waxing Crescent")) return "25%";
        if (phase.contains("First Quarter")) return "50%";
        if (phase.contains("Waxing Gibbous")) return "75%";
        if (phase.contains("Full Moon")) return "100%";
        if (phase.contains("Waning Gibbous")) return "75%";
        if (phase.contains("Last Quarter") || phase.contains("Third Quarter")) return "50%";
        if (phase.contains("Waning Crescent")) return "25%";
        return "15%";
    }

    /**
     * Генерирует центральные иконки погоды, увеличенные ровно в 2 раза (128x128 пикселей).
     */
    private ImageIcon createWeatherIcon(int code) {
        BufferedImage img = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (code == 0) { // Большое красивое Солнце
            g2.setColor(new Color(255, 182, 0));
            g2.fillOval(32, 32, 64, 64);
        } else if (code <= 3) { // Объемные облака
            g2.setColor(new Color(165, 195, 225));
            g2.fillOval(24, 44, 52, 44);
            g2.fillOval(48, 28, 56, 56);
        } else if (code >= 51 && code <= 82) { // Дождевые облака с каплями
            g2.setColor(new Color(140, 165, 195));
            g2.fillOval(32, 28, 64, 48);
            g2.setColor(new Color(30, 144, 255));
            g2.setStroke(new BasicStroke(4)); // Толщина капель больше для масштаба
            g2.drawLine(48, 84, 40, 100);
            g2.drawLine(72, 84, 64, 100);
        } else { // Иконка ошибки
            g2.setColor(new Color(230, 90, 90));
            g2.fillRoundRect(56, 20, 16, 72, 16, 16);
            g2.fillOval(44, 72, 40, 40);
        }

        g2.dispose();
        return new ImageIcon(img);
    }

    private ImageIcon createParamIcon(String type, int angle) {
        BufferedImage img = new BufferedImage(24, 24, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (type.equals("temp")) {
            g2.setColor(new Color(240, 70, 70));
            g2.fillRoundRect(10, 3, 4, 14, 4, 4);
            g2.fillOval(7, 13, 10, 10);
            g2.setColor(Color.WHITE);
            g2.fillOval(11, 15, 3, 3);
        } else if (type.equals("humidity")) {
            g2.setColor(new Color(30, 144, 255));
            g2.fillOval(4, 10, 16, 12);
            int[] xPoints = {4, 12, 20};
            int[] yPoints = {13, 2, 13};
            g2.fillPolygon(xPoints, yPoints, 3);
        } else if (type.equals("wind")) {
            g2.setColor(new Color(180, 185, 195));
            g2.setStroke(new BasicStroke(2));
            g2.drawOval(2, 2, 20, 20);

            g2.translate(12, 12);
            g2.rotate(Math.toRadians(angle));

            g2.setColor(new Color(230, 60, 60));
            int[] xNorth = {0, -4, 0, 4};
            int[] yNorth = {-9, 0, -2, 0};
            g2.fillPolygon(xNorth, yNorth, 4);

            g2.setColor(new Color(70, 130, 240));
            int[] xSouth = {0, -4, 0, 4};
            int[] ySouth = {9, 0, 2, 0};
            g2.fillPolygon(xSouth, ySouth, 4);
        }

        g2.dispose();
        return new ImageIcon(img);
    }

    private ImageIcon createMoonPhaseIcon(String phase) {
        BufferedImage img = new BufferedImage(24, 24, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new Color(215, 215, 220));
        g2.fillOval(2, 2, 20, 20);

        g2.setColor(new Color(255, 225, 110));

        if (phase.contains("Full")) {
            g2.fillOval(2, 2, 20, 20);
        } else if (phase.contains("New")) {
            g2.setColor(new Color(75, 80, 90));
            g2.fillOval(2, 2, 20, 20);
        } else if (phase.contains("First") || phase.contains("Waxing")) {
            g2.fillArc(2, 2, 20, 20, -90, 180);
        } else {
            g2.fillArc(2, 2, 20, 20, 90, 180);
        }

        g2.dispose();
        return new ImageIcon(img);
    }

    public void registerController(WeatherController controller) {
        btnFetch.addActionListener(controller);
    }

    public String getCityInput() { return txtCity.getText().trim(); }
    public void setButtonsEnabled(boolean enabled) { btnFetch.setEnabled(enabled); }
}
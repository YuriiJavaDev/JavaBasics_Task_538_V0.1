package com.yurii.pavlenko.utils;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import javax.swing.*;

/**
 * Utility class for generating dynamic weather and metric icons.
 */
public class WeatherIconPainter {

    private static final int ICON_WIDTH = 80;
    private static final int ICON_HEIGHT = 60;

    public static BufferedImage createIcon(int weatherCode) {
        BufferedImage img = new BufferedImage(ICON_WIDTH, ICON_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        switch (weatherCode) {
            case 0 -> drawSun(g2);
            case 1 -> drawSunBehindCloud(g2);
            case 2, 3 -> drawCloud(g2, new Color(160, 185, 215));
            case 51, 53, 55 -> drawDrizzleIcon(g2);
            case 61, 63, 65, 80, 81, 82 -> drawHeavyRainIcon(g2);
            case 71, 73, 75, 77 -> drawSnowIcon(g2);
            case 95, 96, 99 -> drawThunderstormIcon(g2);
            default -> drawCloud(g2, new Color(160, 185, 215));
        }

        g2.dispose();
        return img;
    }

    public static ImageIcon createThermometerIcon() {
        BufferedImage img = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(230, 80, 80));
        g2.fillOval(10, 20, 12, 12);
        g2.fillRect(14, 4, 4, 18);
        g2.dispose();
        return new ImageIcon(img);
    }

    public static ImageIcon createWindIcon() {
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

    public static ImageIcon createHumidityIcon() {
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

    public static ImageIcon createCompassIcon(int degrees) {
        int size = 32;
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        double cx = size / 2.0;
        double cy = size / 2.0;

        java.awt.geom.Path2D.Double arrow = new java.awt.geom.Path2D.Double();
        arrow.moveTo(cx, 4);
        arrow.lineTo(cx - 8, cy + 4);
        arrow.lineTo(cx, cy);
        arrow.lineTo(cx + 8, cy + 4);
        arrow.closePath();

        java.awt.geom.Path2D.Double tail = new java.awt.geom.Path2D.Double();
        tail.moveTo(cx, cy);
        tail.lineTo(cx - 4, size - 4);
        tail.lineTo(cx + 4, size - 4);
        tail.closePath();

        g2.setColor(new Color(110, 140, 160));
        g2.fill(arrow);
        g2.setColor(new Color(80, 100, 120));
        g2.fill(tail);
        g2.dispose();

        BufferedImage rotated = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gRotated = rotated.createGraphics();
        gRotated.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        gRotated.rotate(Math.toRadians(degrees), cx, cy);
        gRotated.drawImage(img, 0, 0, null);
        gRotated.dispose();

        return new ImageIcon(rotated);
    }

    private static void drawSunBehindCloud(Graphics2D g2) {
        g2.setColor(Color.ORANGE);
        g2.fill(new Ellipse2D.Double(35, 5, 30, 30));
        drawCloud(g2, new Color(160, 185, 215));
    }

    private static void drawDrizzleIcon(Graphics2D g2) {
        drawCloud(g2, new Color(130, 150, 180));
        drawDrizzle(g2);
    }

    private static void drawHeavyRainIcon(Graphics2D g2) {
        drawCloud(g2, new Color(100, 120, 160));
        drawHeavyRain(g2);
    }

    private static void drawSnowIcon(Graphics2D g2) {
        drawCloud(g2, new Color(200, 230, 255));
        drawSnow(g2);
    }

    private static void drawThunderstormIcon(Graphics2D g2) {
        drawCloud(g2, new Color(80, 80, 120));
        drawThickLightning(g2);
        drawHeavyRain(g2);
    }

    private static void drawSun(Graphics2D g2) {
        g2.setColor(Color.ORANGE);
        g2.fill(new Ellipse2D.Double(25, 5, 30, 30));
    }

    private static void drawCloud(Graphics2D g2, Color color) {
        g2.setColor(color);
        g2.fillOval(5, 15, 45, 35);
        g2.fillOval(25, 10, 50, 40);
    }

    private static void drawHeavyRain(Graphics2D g2) {
        g2.setColor(new Color(50, 100, 255));
        g2.setStroke(new BasicStroke(2.5f));
        int[] x = {15, 25, 35, 45, 55};
        for (int xPos : x) {
            g2.drawLine(xPos, 45, xPos - 5, 55);
        }
    }

    private static void drawDrizzle(Graphics2D g2) {
        g2.setColor(new Color(100, 150, 255));
        g2.fillOval(25, 45, 4, 4);
        g2.fillOval(50, 45, 4, 4);
    }

    private static void drawSnow(Graphics2D g2) {
        g2.setColor(Color.WHITE);
        g2.fillOval(25, 45, 6, 6);
        g2.fillOval(50, 45, 6, 6);
    }

    private static void drawThickLightning(Graphics2D g2) {
        int[] x = {38, 44, 33, 39};
        int[] y = {25, 38, 38, 50};
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(4.0f));
        g2.drawPolyline(x, y, 4);
        g2.setColor(Color.YELLOW);
        g2.setStroke(new BasicStroke(2.0f));
        g2.drawPolyline(x, y, 4);
    }
}
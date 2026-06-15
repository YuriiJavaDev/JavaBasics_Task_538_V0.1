package main.com.yurii.pavlenko.model.tools.weather;

/**
 * Data Transfer Object carrying current weather and astronomical metrics for UI rendering.
 */
public class WeatherModelDTO {

    private double temperature;
    private int humidity;
    private double windSpeedMs;
    private String windDirection;
    private int weatherCode;
    private String moonPhase;

    public WeatherModelDTO() {}

    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }

    public int getHumidity() { return humidity; }
    public void setHumidity(int humidity) { this.humidity = humidity; }

    public double getWindSpeedMs() { return windSpeedMs; }
    public void setWindSpeedMs(double windSpeedMs) { this.windSpeedMs = windSpeedMs; }

    public String getWindDirection() { return windDirection; }
    public void setWindDirection(String windDirection) { this.windDirection = windDirection; }

    public int getWeatherCode() { return weatherCode; }
    public void setWeatherCode(int weatherCode) { this.weatherCode = weatherCode; }

    public String getMoonPhase() { return moonPhase; }
    public void setMoonPhase(String moonPhase) { this.moonPhase = moonPhase; }
}
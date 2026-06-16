package main.com.yurii.pavlenko.util;

/**
 * Configuration class holding the Open-Meteo API and Geocoding URL endpoints.
 */
public class WeatherApiConfig {

    public static final String GEOCODING_API_URL =
            "https://geocoding-api.open-meteo.com/v1/search?name=%s&count=1&language=ru";

    public static final String WEATHER_API_URL =
            "https://api.open-meteo.com/v1/forecast" +
                    "?latitude=%s&longitude=%s" +
                    "&current=temperature_2m,relativehumidity_2m,windspeed_10m,winddirection_10m,weathercode";
}
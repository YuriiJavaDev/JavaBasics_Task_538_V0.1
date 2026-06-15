package main.com.yurii.pavlenko.service.tools.weather.impl;

import main.com.yurii.pavlenko.model.tools.weather.WeatherModelDTO;
import main.com.yurii.pavlenko.service.tools.weather.WeatherService;
import main.com.yurii.pavlenko.util.MoonPhaseCalculator;
import main.com.yurii.pavlenko.util.WeatherApiConfig;
import main.com.yurii.pavlenko.util.WindConverter;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of WeatherService pulling data from Open-Meteo REST endpoints.
 */
public class WeatherServiceImpl implements WeatherService {

    private final HttpClient httpClient;

    public WeatherServiceImpl() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    @Override
    public WeatherModelDTO fetchCurrentWeather(String latitude, String longitude) {
        String url = String.format(WeatherApiConfig.WEATHER_API_URL, latitude, longitude);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(5))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("HTTP error code: " + response.statusCode());
            }

            return parseJsonResponse(response.body());

        } catch (Exception e) {
            throw new RuntimeException("Failed to load weather data: " + e.getMessage(), e);
        }
    }

    private WeatherModelDTO parseJsonResponse(String json) {
        WeatherModelDTO dto = new WeatherModelDTO();

        double temp = Double.parseDouble(extractJsonValue(json, "temperature_2m"));
        int humidity = (int) Double.parseDouble(extractJsonValue(json, "relativehumidity_2m"));
        double windKmH = Double.parseDouble(extractJsonValue(json, "windspeed_10m"));
        int windDirDeg = (int) Double.parseDouble(extractJsonValue(json, "winddirection_10m"));
        int weatherCode = (int) Double.parseDouble(extractJsonValue(json, "weathercode"));

        dto.setTemperature(temp);
        dto.setHumidity(humidity);
        dto.setWindSpeedMs(WindConverter.convertKmToMetersPerSecond(windKmH));
        dto.setWindDirection(WindConverter.getWindDirectionArrow(windDirDeg));
        dto.setWeatherCode(weatherCode);
        dto.setMoonPhase(MoonPhaseCalculator.getMoonPhase(LocalDate.now()));

        return dto;
    }

    private String extractJsonValue(String json, String key) {
        Pattern pattern = Pattern.compile("\"" + key + "\"\\s*:\\s*(-?\\d+\\.?\\d*)");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }
        throw new IllegalArgumentException("Key not found in JSON metadata: " + key);
    }
}
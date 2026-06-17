package main.com.yurii.pavlenko.service.tools.weather.impl;

import main.com.yurii.pavlenko.model.tools.weather.WeatherModelDTO;
import main.com.yurii.pavlenko.service.tools.weather.WeatherService;
import main.com.yurii.pavlenko.utils.WeatherApiConfig;
import main.com.yurii.pavlenko.utils.WindConverter;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * Implementation of WeatherService pulling data from Open-Meteo REST endpoints using GSON.
 * Integrates precise wind metrics and handles multi-language geocoding mappings.
 */
public class WeatherServiceImpl implements WeatherService {

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    @Override
    public WeatherModelDTO getWeather(String city) throws Exception {
        // Шаг А: Получаем географические координаты города
        String geoUrl = String.format(WeatherApiConfig.GEOCODING_API_URL,
                URLEncoder.encode(city, StandardCharsets.UTF_8));

        String geoResponse = sendGet(geoUrl);
        JsonObject geoRoot = JsonParser.parseString(geoResponse).getAsJsonObject();
        JsonArray results = geoRoot.getAsJsonArray("results");

        if (results == null || results.size() == 0) {
            throw new Exception("City not found");
        }

        JsonObject location = results.get(0).getAsJsonObject();
        double lat = location.get("latitude").getAsDouble();
        double lon = location.get("longitude").getAsDouble();
        String cityName = location.get("name").getAsString();
        String countryCode = location.get("country_code").getAsString();

        // Шаг Б: По координатам запрашиваем метеоданные
        String weatherUrl = String.format(WeatherApiConfig.WEATHER_API_URL, lat, lon);
        String weatherResponse = sendGet(weatherUrl);
        JsonObject current = JsonParser.parseString(weatherResponse)
                .getAsJsonObject()
                .getAsJsonObject("current");

        double temperature = current.get("temperature_2m").getAsDouble();
        int humidity = current.get("relativehumidity_2m").getAsInt();
        double windKmH = current.get("windspeed_10m").getAsDouble();
        int windDirection = current.get("winddirection_10m").getAsInt();
        int weatherCode = current.get("weathercode").getAsInt();

        // Конвертируем км/ч в м/с через утилиту Clean Code
        double windSpeedMs = WindConverter.convertKmToMetersPerSecond(windKmH);

        // Упаковываем все данные в DTO для UI слоя
        WeatherModelDTO dto = new WeatherModelDTO();
        dto.setCityName(cityName);
        dto.setCountryCode(countryCode);
        dto.setTemperature(temperature);
        dto.setHumidity(humidity);
        dto.setWindSpeed(windSpeedMs);
        dto.setWindDirection(windDirection);
        dto.setWeatherCode(weatherCode);

        return dto;
    }

    private String sendGet(String url) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response =
                httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new Exception("HTTP Error: " + response.statusCode());
        }
        return response.body();
    }

    /**
     * Maps WMO weather codes to plain international text statuses.
     */
    public static String codeToTextStatus(int code) {
        if (code == 0)                return "Clear Sky";
        if (code <= 2)                return "Partly Cloudy";
        if (code == 3)                return "Overcast";
        if (code == 45 || code == 48) return "Foggy";
        if (code >= 51 && code <= 67) return "Rainy";
        if (code >= 71 && code <= 77) return "Snowy";
        if (code >= 80 && code <= 82) return "Heavy Rain";
        if (code == 95)               return "Thunderstorm";
        return "Unknown";
    }

    /**
     * Конвертирует градусы направления ветра в строковый румб.
     */
    public static String degreesToDirection(int degrees) {
        String[] directions = {"N", "NE", "E", "SE", "S", "SW", "W", "NW", "N"};
        return directions[(int) Math.round(((degrees % 360) / 45.0))];
    }
}
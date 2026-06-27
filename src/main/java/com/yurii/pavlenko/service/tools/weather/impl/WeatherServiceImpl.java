package com.yurii.pavlenko.service.tools.weather.impl;

import com.yurii.pavlenko.model.tools.weather.WeatherModelDTO;
import com.yurii.pavlenko.service.tools.weather.WeatherService;
import com.yurii.pavlenko.utils.WeatherApiConfig;
import com.yurii.pavlenko.utils.WeatherCodeMapper;
import com.yurii.pavlenko.utils.WindConverter;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.awt.*;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class WeatherServiceImpl implements WeatherService {

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    @Override
    public WeatherModelDTO getWeather(String city) throws Exception {
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

        String weatherUrl = String.format(WeatherApiConfig.WEATHER_API_URL, lat, lon);
        String weatherResponse = sendGet(weatherUrl);
        JsonObject current = JsonParser.parseString(weatherResponse)
                .getAsJsonObject()
                .getAsJsonObject("current");

        WeatherModelDTO dto = new WeatherModelDTO();
        dto.setCityName(cityName);
        dto.setCountryCode(countryCode);
        dto.setTemperature(current.get("temperature_2m").getAsDouble());
        dto.setHumidity(current.get("relativehumidity_2m").getAsInt());
        dto.setWindSpeed(WindConverter.convertKmToMetersPerSecond(current.get("windspeed_10m").getAsDouble()));
        dto.setWindDirection(current.get("winddirection_10m").getAsInt());
        dto.setWeatherCode(current.get("weathercode").getAsInt());

        return dto;
    }

    private String sendGet(String url) throws Exception {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) throw new Exception("HTTP Error: " + response.statusCode());
        return response.body();
    }
}
package main.com.yurii.pavlenko.service.tools.weather;

import main.com.yurii.pavlenko.model.tools.weather.WeatherModelDTO;

/**
 * Service handling integration with remote meteorology providers.
 */
public interface WeatherService {

    WeatherModelDTO fetchCurrentWeather(String latitude, String longitude);
}
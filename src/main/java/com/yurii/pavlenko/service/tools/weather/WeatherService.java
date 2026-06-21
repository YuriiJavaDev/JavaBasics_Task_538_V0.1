package com.yurii.pavlenko.service.tools.weather;

import com.yurii.pavlenko.model.tools.weather.WeatherModelDTO;

/**
 * Service handling integration with remote meteorology providers.
 */
public interface WeatherService {

    WeatherModelDTO getWeather(String city) throws Exception;
}
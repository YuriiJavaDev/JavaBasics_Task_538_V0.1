package com.yurii.pavlenko.service.tools.weather.impl;

import com.yurii.pavlenko.model.tools.weather.WeatherModelDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class WeatherServiceImplTest {

    private HttpClient mockClient;
    private WeatherServiceImpl weatherService;

    @BeforeEach
    void setUp() {
        mockClient = Mockito.mock(HttpClient.class);
        weatherService = new WeatherServiceImpl(mockClient);
    }

    @Test
    void testGetWeather_SuccessfulResponse() throws Exception {
        HttpResponse<String> geoResponse = createMockResponse(200,
                "{\"results\":[{\"latitude\":31.97,\"longitude\":34.80,\"name\":\"Rishon LeZion\",\"country_code\":\"IL\"}]}");
        HttpResponse<String> weatherResponse = createMockResponse(200,
                "{\"current\":{\"temperature_2m\":25.0,\"relativehumidity_2m\":60,\"windspeed_10m\":10.0,\"winddirection_10m\":180,\"weathercode\":0}}");

        // Використовуємо thenAnswer для обходу проблем компіляції
        when(mockClient.send(any(), any()))
                .thenAnswer(i -> geoResponse)
                .thenAnswer(i -> weatherResponse);

        WeatherModelDTO result = weatherService.getWeather("Rishon LeZion");

        assertNotNull(result);
        assertEquals("Rishon LeZion", result.getCityName());
    }

    @Test
    void testGetWeather_CityNotFoundThrowsException() throws Exception {
        HttpResponse<String> emptyResponse = createMockResponse(200, "{\"results\":[]}");

        when(mockClient.send(any(), any())).thenAnswer(i -> emptyResponse);

        assertThrows(Exception.class, () -> weatherService.getWeather("InvalidCity"));
    }

    @SuppressWarnings("unchecked")
    private HttpResponse<String> createMockResponse(int statusCode, String body) {
        HttpResponse<String> response = Mockito.mock(HttpResponse.class);
        when(response.statusCode()).thenReturn(statusCode);
        when(response.body()).thenReturn(body);
        return response;
    }
}
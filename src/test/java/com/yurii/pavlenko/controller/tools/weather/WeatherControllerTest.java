package com.yurii.pavlenko.controller.tools.weather;

import com.yurii.pavlenko.model.tools.weather.WeatherModelDTO;
import com.yurii.pavlenko.service.tools.weather.WeatherService;
import com.yurii.pavlenko.ui.panels.tools.WeatherPanel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javax.swing.SwingWorker;
import static org.mockito.Mockito.*;

class WeatherControllerTest {

    private WeatherService mockService;
    private WeatherPanel mockView;

    private static class TestWorker extends SwingWorker<WeatherModelDTO, Void> {
        private final WeatherService service;
        private final String city;

        public TestWorker(WeatherService service, String city) {
            this.service = service;
            this.city = city;
        }

        @Override
        public WeatherModelDTO doInBackground() throws Exception {
            return service.getWeather(city);
        }

        @Override
        protected void done() {}
    }

    @BeforeEach
    void setUp() {
        mockService = mock(WeatherService.class);
        mockView = mock(WeatherPanel.class);
    }

    @Test
    void testActionPerformed_TriggersFetch() throws Exception {
        WeatherModelDTO mockData = new WeatherModelDTO();
        mockData.setCityName("Kyiv");
        when(mockService.getWeather("Kyiv")).thenReturn(mockData);
        when(mockView.getCityInput()).thenReturn("Kyiv");

        WeatherController controller = new WeatherController(mockService, mockView) {
            @Override
            protected SwingWorker<WeatherModelDTO, Void> createWeatherWorker(String city) {
                return new TestWorker(mockService, city);
            }
        };

        TestWorker worker = (TestWorker) controller.createWeatherWorker("Kyiv");
        worker.doInBackground();

        controller.actionPerformed(null);
        verify(mockService, atLeastOnce()).getWeather("Kyiv");
    }

    @Test
    void testInitialization_FetchesDefaultCity() throws Exception {
        when(mockView.getCityInput()).thenReturn("");

        WeatherController controller = new WeatherController(mockService, mockView) {
            @Override
            protected SwingWorker<WeatherModelDTO, Void> createWeatherWorker(String city) {
                return new TestWorker(mockService, city);
            }
        };

        TestWorker worker = (TestWorker) controller.createWeatherWorker("Rishon LeZion");
        worker.doInBackground();

        verify(mockService, atLeastOnce()).getWeather("Rishon LeZion");
    }
}
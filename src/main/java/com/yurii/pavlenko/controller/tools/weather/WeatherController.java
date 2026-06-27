package com.yurii.pavlenko.controller.tools.weather;

import com.yurii.pavlenko.model.tools.weather.WeatherModelDTO;
import com.yurii.pavlenko.service.tools.weather.WeatherService;
import com.yurii.pavlenko.ui.panels.tools.WeatherPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.SwingWorker;

public class WeatherController implements ActionListener {
    private final WeatherService service;
    private final WeatherPanel view;

    public WeatherController(WeatherService service, WeatherPanel view) {
        this.service = service;
        this.view = view;
        this.view.registerController(this);
        executeWeatherFetch();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        executeWeatherFetch();
    }

    private void executeWeatherFetch() {
        String inputCity = view.getCityInput();
        final String cityToFetch = (inputCity == null || inputCity.isEmpty()) ? "Rishon LeZion" : inputCity;

        view.setButtonsEnabled(false);
        view.displayError("Loading...");

        createWeatherWorker(cityToFetch).execute();
    }

    protected SwingWorker<WeatherModelDTO, Void> createWeatherWorker(String city) {
        return new SwingWorker<>() {
            @Override
            protected WeatherModelDTO doInBackground() throws Exception {
                return service.getWeather(city);
            }

            @Override
            protected void done() {
                try {
                    WeatherModelDTO data = get();
                    if (data != null) {
                        view.updateWeatherDisplay(data);
                    } else {
                        view.displayError("City not found");
                    }
                } catch (Exception ex) {
                    view.displayError("City not found");
                } finally {
                    view.setButtonsEnabled(true);
                }
            }
        };
    }
}
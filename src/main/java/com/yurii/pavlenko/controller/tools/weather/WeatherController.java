package com.yurii.pavlenko.controller.tools.weather;

import com.yurii.pavlenko.model.tools.weather.WeatherModelDTO;
import com.yurii.pavlenko.service.tools.weather.WeatherService;
import com.yurii.pavlenko.ui.panels.tools.WeatherPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.SwingWorker;

/**
 * Controller managing weather update sequences using multi-threaded Swing worker patterns.
 * Coordinates data flow and triggers dynamic astronomical moon phase recalculations.
 */
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
        String city = view.getCityInput();

        if (city.isEmpty()) {
            view.displayError("Specify the city");
            return;
        }

        view.setButtonsEnabled(false);

        // Launching SwingWorker: HTTP goes into the background, EDT continues to spin the interface without freezing.
        new SwingWorker<WeatherModelDTO, Void>() {
            @Override
            protected WeatherModelDTO doInBackground() throws Exception {
                return service.getWeather(city);
            }

            @Override
            protected void done() {
                try {
                    WeatherModelDTO result = get();
                    view.updateWeatherDisplay(result);

                } catch (Exception ex) {
                    String message = ex.getCause() != null ? ex.getCause().getMessage() : "Network error";
                    view.displayError(message);
                } finally {
                    view.setButtonsEnabled(true);
                }
            }
        }.execute();
    }
}
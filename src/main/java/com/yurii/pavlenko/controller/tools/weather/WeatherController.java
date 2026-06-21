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
            view.displayError("Укажите город");
            return;
        }

        view.setButtonsEnabled(false);

        // Запуск SwingWorker: HTTP уходит в фон, а EDT продолжает крутить интерфейс без фризов
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
                    String message = ex.getCause() != null ? ex.getCause().getMessage() : "Ошибка сети";
                    view.displayError(message);
                } finally {
                    view.setButtonsEnabled(true);
                }
            }
        }.execute();
    }
}
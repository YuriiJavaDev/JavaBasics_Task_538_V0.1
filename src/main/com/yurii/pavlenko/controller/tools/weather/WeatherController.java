package main.com.yurii.pavlenko.controller.tools.weather;

import main.com.yurii.pavlenko.model.tools.weather.WeatherModelDTO;
import main.com.yurii.pavlenko.service.tools.weather.WeatherService;
import main.com.yurii.pavlenko.ui.panels.tools.WeatherPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.SwingWorker;

/**
 * Controller managing weather update sequences using multi-threaded Swing worker patterns.
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
        String lat = view.getLatitudeInput();
        String lon = view.getLongitudeInput();

        if (lat.isEmpty() || lon.isEmpty()) {
            view.displayError("Empty coordinates");
            return;
        }

        view.setButtonsEnabled(false);

        new SwingWorker<WeatherModelDTO, Void>() {
            @Override
            protected WeatherModelDTO doInBackground() {
                return service.fetchCurrentWeather(lat, lon);
            }

            @Override
            protected void done() {
                try {
                    WeatherModelDTO result = get();
                    view.updateWeatherDisplay(result);
                } catch (Exception ex) {
                    view.displayError("Connection failed");
                } finally {
                    view.setButtonsEnabled(true);
                }
            }
        }.execute();
    }
}
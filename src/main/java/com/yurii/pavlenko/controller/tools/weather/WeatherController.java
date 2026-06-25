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

        new SwingWorker<WeatherModelDTO, Void>() {
            @Override
            protected WeatherModelDTO doInBackground() throws Exception {
                return service.getWeather(cityToFetch);
            }
            @Override protected void done() {
                try { view.updateWeatherDisplay(get()); }
                catch (Exception ex) { view.displayError("City not found"); }
                finally { view.setButtonsEnabled(true); }
            }
        }.execute();
    }
}
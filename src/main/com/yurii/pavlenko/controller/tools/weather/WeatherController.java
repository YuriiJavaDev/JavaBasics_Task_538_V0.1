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
        String city = view.getCityInput();

        if (city.isEmpty()) {
            view.displayError("Укажите город");
            return;
        }

        view.setButtonsEnabled(false);

        // Запуск SwingWorker: HTTP уходит в фон, а EDT продолжает крутить интерфейс
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
                    // Достаем реальное текстовое сообщение об ошибке, прилетевшее из сервиса
                    String message = ex.getCause() != null ? ex.getCause().getMessage() : "Ошибка сети";
                    view.displayError(message);
                } finally {
                    view.setButtonsEnabled(true);
                }
            }
        }.execute();
    }
}
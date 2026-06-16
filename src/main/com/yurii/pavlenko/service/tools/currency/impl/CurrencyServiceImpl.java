package main.com.yurii.pavlenko.service.tools.currency.impl;

import main.com.yurii.pavlenko.model.tools.currency.CurrencyModelDTO;
import main.com.yurii.pavlenko.service.tools.currency.CurrencyService;
import main.com.yurii.pavlenko.util.CurrencyApiConfig;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Implementation pulling actual currencies from exchange API and calculating precise cross-rates.
 */
public class CurrencyServiceImpl implements CurrencyService {

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    @Override
    public CurrencyModelDTO convert(String from, String to, double amount) throws Exception {
        // Шаг А: Отправляем GET-запрос к API котировок
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(CurrencyApiConfig.EXCHANGE_API_URL))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new Exception("HTTP Financial API Error: " + response.statusCode());
        }

        // Шаг Б: Парсим JSON объект через GSON
        JsonObject root = JsonParser.parseString(response.body()).getAsJsonObject();
        JsonObject ratesObject = root.getAsJsonObject("rates");

        if (ratesObject == null) {
            throw new Exception("Invalid financial data format");
        }

        // Шаг В: Извлекаем рейты относительно USD
        double rateFrom = ratesObject.get(from).getAsDouble();
        double rateTo = ratesObject.get(to).getAsDouble();

        // Шаг Г: Рассчитываем точный кросс-курс и финальную сумму
        // Так как все курсы даны к USD (например, 1 USD = X от From, 1 USD = Y от To)
        double amountInUsd = amount / rateFrom;
        double finalResult = amountInUsd * rateTo;

        // Сохраняем все данные в DTO
        CurrencyModelDTO dto = new CurrencyModelDTO();
        dto.setBaseCurrency(from);
        dto.setTargetCurrency(to);
        dto.setAmount(amount);
        dto.setCalculatedResult(finalResult);

        return dto;
    }
}
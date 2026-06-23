package com.yurii.pavlenko.service.tools.currency.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.yurii.pavlenko.model.tools.currency.CurrencyModelDTO;
import com.yurii.pavlenko.service.tools.currency.CurrencyService;
import com.yurii.pavlenko.utils.CurrencyApiConfig;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class CurrencyServiceImpl implements CurrencyService {

    private final HttpClient httpClient;

    public CurrencyServiceImpl() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    public CurrencyServiceImpl(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public CurrencyModelDTO convert(String from, String to, double amount) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(CurrencyApiConfig.EXCHANGE_API_URL))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new Exception("HTTP API Error: " + response.statusCode());
        }

        JsonObject root = JsonParser.parseString(response.body()).getAsJsonObject();
        JsonObject ratesObject = root.getAsJsonObject("rates");

        if (ratesObject == null) {
            throw new Exception("Invalid data format");
        }

        double rateFrom = ratesObject.get(from).getAsDouble();
        double rateTo = ratesObject.get(to).getAsDouble();

        double amountInUsd = amount / rateFrom;
        double finalResult = amountInUsd * rateTo;

        CurrencyModelDTO dto = new CurrencyModelDTO();
        dto.setBaseCurrency(from);
        dto.setTargetCurrency(to);
        dto.setAmount(amount);
        dto.setCalculatedResult(finalResult);

        return dto;
    }
}
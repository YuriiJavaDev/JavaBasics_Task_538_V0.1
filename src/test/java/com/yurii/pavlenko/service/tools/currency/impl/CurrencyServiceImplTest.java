package com.yurii.pavlenko.service.tools.currency.impl;

import com.yurii.pavlenko.model.tools.currency.CurrencyModelDTO;
import org.junit.jupiter.api.Test;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class CurrencyServiceImplTest {

    @Test
    void convertShouldCalculateCorrectRate() throws Exception {
        String jsonResponse = "{\"rates\": {\"USD\": 1.0, \"EUR\": 0.9, \"GBP\": 0.8}}";

        HttpClient mockClient = mock(HttpClient.class);
        HttpResponse<String> mockResponse = mock(HttpResponse.class);

        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn(jsonResponse);

        when(mockClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        CurrencyServiceImpl service = new CurrencyServiceImpl(mockClient);
        CurrencyModelDTO result = service.convert("EUR", "GBP", 10.0);

        assertEquals(8.88888888888889, result.getCalculatedResult(), 0.0001);
    }
}
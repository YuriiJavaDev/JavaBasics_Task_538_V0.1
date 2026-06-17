package main.com.yurii.pavlenko.utils;

/**
 * Configuration constants for the Currency Converter feature.
 * Uses public open exchange rates API endpoint.
 */
public class CurrencyApiConfig {

    // Публичный бесплатный API не требующий ключей, возвращает рейты относительно USD
    public static final String EXCHANGE_API_URL = "https://open.er-api.com/v6/latest/USD";

    // Набор базовых популярных валют для выпадающих списков
    public static final String[] SUPPORTED_CURRENCIES = {
            "USD", "EUR", "UAH", "ILS", "GBP", "JPY", "CAD", "AUD"
    };

    private CurrencyApiConfig() {
        // Предотвращаем инстанцирование утильного класса
    }
}
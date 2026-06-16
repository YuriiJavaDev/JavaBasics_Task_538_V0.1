package main.com.yurii.pavlenko.service.tools.currency;

import main.com.yurii.pavlenko.model.tools.currency.CurrencyModelDTO;

/**
 * Contract specifying financial conversion and exchange rate logic.
 */
public interface CurrencyService {

    /**
     * Converts an amount from one currency to another using live REST data.
     */
    CurrencyModelDTO convert(String from, String to, double amount) throws Exception;
}
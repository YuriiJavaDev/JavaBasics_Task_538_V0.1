package com.yurii.pavlenko.model.tools.currency;

import java.util.Map;

/**
 * Data Transfer Object containing currency codes, base rates, and calculation results.
 */
public class CurrencyModelDTO {

    private String baseCurrency;
    private String targetCurrency;
    private double amount;
    private double calculatedResult;
    private Map<String, Double> allRates;

    public String getBaseCurrency() { return baseCurrency; }
    public void setBaseCurrency(String baseCurrency) { this.baseCurrency = baseCurrency; }

    public String getTargetCurrency() { return targetCurrency; }
    public void setTargetCurrency(String targetCurrency) { this.targetCurrency = targetCurrency; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public double getCalculatedResult() { return calculatedResult; }
    public void setCalculatedResult(double calculatedResult) { this.calculatedResult = calculatedResult; }

    public Map<String, Double> getAllRates() { return allRates; }
    public void setAllRates(Map<String, Double> allRates) { this.allRates = allRates; }
}
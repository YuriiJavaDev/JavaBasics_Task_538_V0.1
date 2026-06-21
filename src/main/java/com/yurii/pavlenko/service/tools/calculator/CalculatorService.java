package com.yurii.pavlenko.service.tools.calculator;

/**
 * Service interface handling arithmetic and engineering computational logic.
 */
public interface CalculatorService {

    double calculateBinary(double firstOperand, double secondOperand, String operator);

    double calculateUnary(double operand, String operation, boolean isRadians);

    // void saveToMemory(double value);

    void clearMemory();

    double getMemoryValue();

    void addToMemory(double value);

    double calculateExpression(String expression, boolean isRadians);
}
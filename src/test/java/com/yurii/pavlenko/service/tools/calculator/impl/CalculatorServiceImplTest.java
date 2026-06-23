package com.yurii.pavlenko.service.tools.calculator.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CalculatorServiceImplTest {

    private CalculatorServiceImpl calculatorService;

    @BeforeEach
    void setUp() {
        calculatorService = new CalculatorServiceImpl();
    }

    @Test
    void shouldDelegateMemoryOperationsCorrectly() {
        calculatorService.addToMemory(25.0);
        assertEquals(25.0, calculatorService.getMemoryValue(), 0.0001);

        calculatorService.clearMemory();
        assertEquals(0.0, calculatorService.getMemoryValue(), 0.0001);
    }

    @Test
    void shouldDelegateBinaryCalculationCorrectly() {
        double result = calculatorService.calculateBinary(10.0, 2.0, "/");
        assertEquals(5.0, result, 0.0001);
    }

    @Test
    void shouldDelegateUnaryCalculationCorrectly() {
        double result = calculatorService.calculateUnary(9.0, "sqrt", false);
        assertEquals(3.0, result, 0.0001);
    }

    @Test
    void shouldDelegateExpressionCalculationCorrectly() {
        double result = calculatorService.calculateExpression("2 + 3 * 4", false);
        assertEquals(14.0, result, 0.0001);
    }
}
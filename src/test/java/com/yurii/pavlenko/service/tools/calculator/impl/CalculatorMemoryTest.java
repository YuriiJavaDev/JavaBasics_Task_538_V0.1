package com.yurii.pavlenko.service.tools.calculator.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CalculatorMemoryTest {

    private CalculatorMemory calculatorMemory;

    @BeforeEach
    void setUp() {
        calculatorMemory = new CalculatorMemory();
    }

    @Test
    void shouldHaveZeroInitially() {
        assertEquals(0.0, calculatorMemory.getMemoryValue(), 0.0001);
    }

    @Test
    void shouldCorrectlyAddToMemory() {
        calculatorMemory.addToMemory(10.5);
        calculatorMemory.addToMemory(5.5);

        assertEquals(16.0, calculatorMemory.getMemoryValue(), 0.0001);
    }

    @Test
    void shouldCorrectlyClearMemory() {
        calculatorMemory.addToMemory(99.9);
        calculatorMemory.clearMemory();

        assertEquals(0.0, calculatorMemory.getMemoryValue(), 0.0001);
    }
}
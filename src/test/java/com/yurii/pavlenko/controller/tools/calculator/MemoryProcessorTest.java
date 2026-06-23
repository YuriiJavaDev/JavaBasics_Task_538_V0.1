package com.yurii.pavlenko.controller.tools.calculator;

import com.yurii.pavlenko.model.tools.calculator.CalculatorModel;
import com.yurii.pavlenko.service.tools.calculator.CalculatorService;
import com.yurii.pavlenko.ui.panels.tools.CalculatorDisplay;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.event.ActionListener;

import static org.junit.jupiter.api.Assertions.*;

class MemoryProcessorTest {
    private CalculatorModel model;
    private FakeDisplay fakeDisplay;
    private StubService stubService;
    private MemoryProcessor memoryProcessor;

    private static class FakeDisplay implements CalculatorDisplay {
        String memText = "";
        @Override public void updateDisplay(String text) {}
        @Override public void updateFormulaDisplay(String text) {}
        @Override public void updateMemoryDisplay(String text) { memText = text; }
        @Override
        public void registerController(ActionListener controller) {
        }
    }

    private static class StubService implements CalculatorService {
        double mem = 0.0;
        @Override public void addToMemory(double v) { mem += v; }
        @Override public double getMemoryValue() { return mem; }
        @Override public void clearMemory() { mem = 0.0; }
        // Інші методи заглушаємо...
        @Override public double calculateBinary(double a, double b, String op) { return 0; }
        @Override public double calculateUnary(double v, String op, boolean r) { return 0; }
        @Override public double calculateExpression(String e, boolean r) { return 0; }
    }

    @BeforeEach
    void setUp() {
        model = new CalculatorModel();
        fakeDisplay = new FakeDisplay();
        stubService = new StubService();
        memoryProcessor = new MemoryProcessor(model, stubService, fakeDisplay, new ResultFormatter());
    }

    @Test
    void shouldAddCurrentInputToMemory() {
        model.setCurrentInput("10.5");
        memoryProcessor.processMemoryAdd();
        assertEquals(10.5, stubService.getMemoryValue());
        assertEquals("10.5", fakeDisplay.memText);
    }

    @Test
    void shouldClearMemory() {
        stubService.addToMemory(50);
        memoryProcessor.processMemoryClear(new StringBuilder("50"));
        assertEquals(0.0, stubService.getMemoryValue());
        assertEquals("0", fakeDisplay.memText);
    }
}
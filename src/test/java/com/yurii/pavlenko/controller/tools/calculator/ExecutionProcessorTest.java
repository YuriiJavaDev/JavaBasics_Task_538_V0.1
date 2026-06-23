package com.yurii.pavlenko.controller.tools.calculator;

import com.yurii.pavlenko.model.tools.calculator.CalculatorModel;
import com.yurii.pavlenko.service.tools.calculator.CalculatorService;
import com.yurii.pavlenko.ui.panels.tools.CalculatorDisplay;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.event.ActionListener;

import static org.junit.jupiter.api.Assertions.*;

class ExecutionProcessorTest {

    private CalculatorModel model;
    private FakeCalculatorDisplay fakeDisplay;
    private StubCalculatorService stubService;
    private ResultFormatter formatter;
    private ExecutionProcessor executionProcessor;

    private static class FakeCalculatorDisplay implements CalculatorDisplay {
        String lastDisplay = "";
        String lastFormulaDisplay = "";

        @Override
        public void updateDisplay(String text) {
            this.lastDisplay = text;
        }

        @Override
        public void updateFormulaDisplay(String text) {
            this.lastFormulaDisplay = text;
        }
        @Override public void updateMemoryDisplay(String text) {
        }
        @Override
        public void registerController(ActionListener controller) {
        }
    }

    private static class StubCalculatorService implements CalculatorService {
        double stubResult = 0.0;
        boolean shouldThrowException = false;
        String exceptionMessage = "";

        @Override
        public double calculateExpression(String expression, boolean isRadians) {
            if (shouldThrowException) {
                throw new IllegalArgumentException(exceptionMessage);
            }
            return stubResult;
        }

        @Override
        public double calculateBinary(double firstOperand, double secondOperand, String operator) {
            return 0;
        }

        @Override
        public double calculateUnary(double operand, String operation, boolean isRadians) {
            return 0;
        }

        @Override
        public void clearMemory() {
        }

        @Override
        public double getMemoryValue() {
            return 0.0;
        }

        @Override
        public void addToMemory(double value) {
        }
    }

    @BeforeEach
    void setUp() {
        model = new CalculatorModel();
        fakeDisplay = new FakeCalculatorDisplay();
        stubService = new StubCalculatorService();
        formatter = new ResultFormatter();

        executionProcessor = new ExecutionProcessor(model, stubService, fakeDisplay, formatter);
    }

    @Test
    void shouldDoNothingIfExpressionIsEmpty() {
        StringBuilder expressionBuilder = new StringBuilder();

        executionProcessor.processExpressionCalculate(expressionBuilder);

        assertEquals(0, expressionBuilder.length());
        assertTrue(model.isAwaitingNewInput());
    }

    @Test
    void shouldAutomaticallyCloseMissingBracketsAndCalculate() {
        StringBuilder expressionBuilder = new StringBuilder("2 * (3 + 5");
        stubService.stubResult = 16.0;

        executionProcessor.processExpressionCalculate(expressionBuilder);

        assertEquals("16", model.getCurrentInput());
        assertEquals(16.0, model.getLastResult());
        assertTrue(model.isAwaitingNewInput());
        assertEquals("16", fakeDisplay.lastDisplay);
        assertEquals("2 * (3 + 5) = 16", fakeDisplay.lastFormulaDisplay);
        assertEquals(0, expressionBuilder.length());
    }

    @Test
    void shouldCorrectlyHandleArithmeticException() {
        StringBuilder expressionBuilder = new StringBuilder("5 / 0");
        stubService.shouldThrowException = true;
        stubService.exceptionMessage = "Division by zero";

        executionProcessor.processExpressionCalculate(expressionBuilder);

        assertEquals("Error: Division by zero", fakeDisplay.lastDisplay);
        assertEquals("5 / 0 = Error", fakeDisplay.lastFormulaDisplay);
        assertTrue(model.isAwaitingNewInput());
        assertEquals(0, expressionBuilder.length());
    }
}
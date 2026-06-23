package com.yurii.pavlenko.controller.tools.calculator;

import com.yurii.pavlenko.model.tools.calculator.CalculatorModel;
import com.yurii.pavlenko.ui.panels.tools.CalculatorDisplay;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.event.ActionListener;

import static org.junit.jupiter.api.Assertions.*;

class OperatorProcessorTest {

    private CalculatorModel model;
    private FakeCalculatorDisplay fakeDisplay;
    private OperatorProcessor operatorProcessor;

    private static class FakeCalculatorDisplay implements CalculatorDisplay {
        String lastDisplay = "";
        String lastFormulaDisplay = "";
        @Override public void updateDisplay(String text) { lastDisplay = text; }
        @Override public void updateFormulaDisplay(String text) { lastFormulaDisplay = text; }
        @Override public void updateMemoryDisplay(String text) {
        }
        @Override
        public void registerController(ActionListener controller) {
        }
    }

    @BeforeEach
    void setUp() {
        model = new CalculatorModel();
        fakeDisplay = new FakeCalculatorDisplay();
        operatorProcessor = new OperatorProcessor(model, fakeDisplay);
    }

    @Test
    void shouldHandleNegativeNumberAtStart() {
        model.setCurrentInput("0");
        StringBuilder expr = new StringBuilder();

        operatorProcessor.processMinusOperator(expr);

        assertEquals("-", expr.toString());
        assertEquals("-", model.getCurrentInput());
        assertFalse(model.isAwaitingNewInput());
    }

    @Test
    void shouldAddOperatorWithSpaces() {
        model.setCurrentInput("10");
        StringBuilder expr = new StringBuilder("10");

        operatorProcessor.processExpressionOperator("+", expr);

        assertEquals("10 + ", expr.toString());
        assertTrue(model.isAwaitingNewInput());
    }

    @Test
    void shouldHandleMinusAfterBracket() {
        StringBuilder expr = new StringBuilder("(");

        operatorProcessor.processMinusOperator(expr);

        assertEquals("(-", expr.toString());
        assertEquals("-", model.getCurrentInput());
    }
}
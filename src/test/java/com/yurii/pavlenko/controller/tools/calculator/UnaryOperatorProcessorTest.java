package com.yurii.pavlenko.controller.tools.calculator;

import com.yurii.pavlenko.model.tools.calculator.CalculatorModel;
import com.yurii.pavlenko.ui.panels.tools.CalculatorDisplay;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.event.ActionListener;

import static org.junit.jupiter.api.Assertions.*;

class UnaryOperatorProcessorTest {

    private CalculatorModel model;
    private FakeCalculatorDisplay fakeDisplay;
    private UnaryOperatorProcessor unaryProcessor;

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
        unaryProcessor = new UnaryOperatorProcessor(model, fakeDisplay);
    }

    @Test
    void shouldHandlePostfixSquare() {
        model.setCurrentInput("5");
        StringBuilder expr = new StringBuilder("5");

        unaryProcessor.processUnaryOperator("x²", expr);

        assertEquals("5^2", expr.toString());
        assertEquals("x²", fakeDisplay.lastDisplay);
    }

    @Test
    void shouldReplaceInputWithTextFunction() {
        model.setCurrentInput("10");
        StringBuilder expr = new StringBuilder("10");

        unaryProcessor.processUnaryOperator("sin", expr);

        assertEquals("sin(", expr.toString());
        assertEquals("sin(", fakeDisplay.lastDisplay);
    }
}
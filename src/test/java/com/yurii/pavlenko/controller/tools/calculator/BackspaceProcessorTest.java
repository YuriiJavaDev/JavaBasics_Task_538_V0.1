package com.yurii.pavlenko.controller.tools.calculator;

import com.yurii.pavlenko.model.tools.calculator.CalculatorModel;
import com.yurii.pavlenko.ui.panels.tools.CalculatorDisplay;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.event.ActionListener;

import static org.junit.jupiter.api.Assertions.*;

class BackspaceProcessorTest {

    private CalculatorModel model;
    private FakeCalculatorDisplay fakeDisplay;
    private InputProcessor inputProcessor;
    private BackspaceProcessor backspaceProcessor;

    // Наша перевірена заглушка для дисплея
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

        @Override
        public void updateMemoryDisplay(String text) {
        }

        @Override
        public void registerController(ActionListener controller) {
        }
    }

    @BeforeEach
    void setUp() {
        model = new CalculatorModel();
        fakeDisplay = new FakeCalculatorDisplay();
        // Створюємо реальний InputProcessor, оскільки він потрібен для методу processClear
        inputProcessor = new InputProcessor(model, fakeDisplay);
        backspaceProcessor = new BackspaceProcessor(model, fakeDisplay, inputProcessor);
    }

    @Test
    void shouldRemoveWholeModOperator() {
        StringBuilder exprBuilder = new StringBuilder("10 mod ");

        backspaceProcessor.processBackspace(exprBuilder);

        assertEquals("10", exprBuilder.toString());
        assertEquals("10", fakeDisplay.lastDisplay);
        assertEquals("10", model.getCurrentInput());
    }

    @Test
    void shouldRemoveBinaryOperatorWithSpaces() {
        StringBuilder exprBuilder = new StringBuilder("5 + ");

        backspaceProcessor.processBackspace(exprBuilder);

        assertEquals("5", exprBuilder.toString());
        assertEquals("5", fakeDisplay.lastDisplay);
    }

    @Test
    void shouldRemoveUnaryFunctionLikeSin() {
        StringBuilder exprBuilder = new StringBuilder("sin(");

        backspaceProcessor.processBackspace(exprBuilder);

        // Рядок стає порожнім, тому має спрацювати метод очищення (скидання в "0")
        assertEquals(0, exprBuilder.length());
        assertEquals("0", fakeDisplay.lastDisplay);
    }

    @Test
    void shouldRemoveLastCharacterOfCurrentInputIfExpressionIsEmpty() {
        StringBuilder exprBuilder = new StringBuilder();
        model.setCurrentInput("125");

        backspaceProcessor.processBackspace(exprBuilder);

        assertEquals("12", model.getCurrentInput());
        assertEquals("12", fakeDisplay.lastDisplay);
    }

    @Test
    void shouldClearEverythingIfOnlyMinusSignIsLeft() {
        StringBuilder exprBuilder = new StringBuilder();
        model.setCurrentInput("-");

        backspaceProcessor.processBackspace(exprBuilder);

        assertEquals("0", fakeDisplay.lastDisplay);
        assertTrue(model.isAwaitingNewInput());
    }
}
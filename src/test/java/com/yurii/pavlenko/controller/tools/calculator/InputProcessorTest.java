package com.yurii.pavlenko.controller.tools.calculator;

import com.yurii.pavlenko.model.tools.calculator.CalculatorModel;
import com.yurii.pavlenko.ui.panels.tools.CalculatorDisplay;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.event.ActionListener;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InputProcessorTest {

    private CalculatorModel model;
    private FakeCalculatorDisplay fakeDisplay;
    private InputProcessor inputProcessor;

    // Створюємо власну просту заглушку замість примхливого Mockito
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

    @BeforeEach
    void setUp() {
        model = new CalculatorModel();
        fakeDisplay = new FakeCalculatorDisplay();
        inputProcessor = new InputProcessor(model, fakeDisplay);
    }

    @Test
    void shouldClearModelAndExpressionsOnProcessClear() {
        StringBuilder expressionBuilder = new StringBuilder("2 + 3");
        model.setCurrentInput("5");

        inputProcessor.processClear(expressionBuilder);

        assertEquals(0, expressionBuilder.length());
        assertEquals("0", model.getCurrentInput());
        assertEquals("0", fakeDisplay.lastDisplay);
        assertEquals(" ", fakeDisplay.lastFormulaDisplay);
    }

    @Test
    void shouldAppendDigitWhenNotAwaitingNewInput() {
        StringBuilder expressionBuilder = new StringBuilder("5");
        model.setCurrentInput("5");
        model.setAwaitingNewInput(false);

        inputProcessor.processDigitWithResetCheck("2", expressionBuilder);

        assertEquals("52", model.getCurrentInput());
        assertEquals("52", expressionBuilder.toString());
        assertEquals("52", fakeDisplay.lastDisplay);
    }

    @Test
    void shouldSetInitialDigitWhenAwaitingNewInput() {
        StringBuilder expressionBuilder = new StringBuilder();
        model.setCurrentInput("0");
        model.setAwaitingNewInput(true);

        inputProcessor.processDigitWithResetCheck("7", expressionBuilder);

        assertEquals("7", model.getCurrentInput());
        assertEquals("7", expressionBuilder.toString());
        assertEquals("7", fakeDisplay.lastDisplay);
    }

    @Test
    void shouldCorrectlyHandleFirstDotInput() {
        StringBuilder expressionBuilder = new StringBuilder("5");
        model.setCurrentInput("5");
        model.setAwaitingNewInput(false);

        inputProcessor.processDot(expressionBuilder);

        assertEquals("5.", model.getCurrentInput());
        assertEquals("5.", expressionBuilder.toString());
        assertEquals("5.", fakeDisplay.lastDisplay);
    }

    @Test
    void shouldIgnoreDotIfAlreadyExistsInCurrentInput() {
        StringBuilder expressionBuilder = new StringBuilder("5.5");
        model.setCurrentInput("5.5");
        model.setAwaitingNewInput(false);

        inputProcessor.processDot(expressionBuilder);

        assertEquals("5.5", model.getCurrentInput());
        assertEquals("5.5.", expressionBuilder.toString());
    }

    @Test
    void shouldAppendConstantCorrectly() {
        StringBuilder expressionBuilder = new StringBuilder("2 + ");

        inputProcessor.processConstant(Math.PI, expressionBuilder);

        assertEquals("3.141592653589793", model.getCurrentInput());
        assertEquals("2 + 3.141592653589793", expressionBuilder.toString());
        assertEquals("3.141592653589793", fakeDisplay.lastDisplay);
    }
}
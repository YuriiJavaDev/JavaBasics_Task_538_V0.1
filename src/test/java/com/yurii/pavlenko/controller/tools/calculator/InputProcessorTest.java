package com.yurii.pavlenko.controller.tools.calculator;

import com.yurii.pavlenko.model.tools.calculator.CalculatorModel;
import com.yurii.pavlenko.ui.panels.tools.CalculatorPanel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class InputProcessorTest {

    private CalculatorModel model;
    private CalculatorPanel viewMock;
    private InputProcessor inputProcessor;

    @BeforeEach
    void setUp() {
        // Using real object for model and mock for UI panel
        model = new CalculatorModel();
        viewMock = mock(CalculatorPanel.class);
        inputProcessor = new InputProcessor(model, viewMock);
    }

    @Test
    void shouldClearModelAndExpressionsOnProcessClear() {
        StringBuilder expressionBuilder = new StringBuilder("2 + 3");
        model.setCurrentInput("5");

        inputProcessor.processClear(expressionBuilder);

        assertEquals(0, expressionBuilder.length());
        assertEquals("0", model.getCurrentInput());
        verify(viewMock, times(1)).updateDisplay("0");
        verify(viewMock, times(1)).updateFormulaDisplay(" ");
    }

    @Test
    void shouldAppendDigitWhenNotAwaitingNewInput() {
        StringBuilder expressionBuilder = new StringBuilder("5");
        model.setCurrentInput("5");
        model.setAwaitingNewInput(false);

        inputProcessor.processDigitWithResetCheck("2", expressionBuilder);

        assertEquals("52", model.getCurrentInput());
        assertEquals("52", expressionBuilder.toString());
        verify(viewMock, times(1)).updateDisplay("52");
    }

    @Test
    void shouldSetInitialDigitWhenAwaitingNewInput() {
        StringBuilder expressionBuilder = new StringBuilder();
        model.setCurrentInput("0");
        model.setAwaitingNewInput(true);

        inputProcessor.processDigitWithResetCheck("7", expressionBuilder);

        assertEquals("7", model.getCurrentInput());
        assertEquals("7", expressionBuilder.toString());
        verify(viewMock, times(1)).updateDisplay("7");
    }

    @Test
    void shouldCorrectlyHandleFirstDotInput() {
        StringBuilder expressionBuilder = new StringBuilder("5");
        model.setCurrentInput("5");
        model.setAwaitingNewInput(false);

        inputProcessor.processDot(expressionBuilder);

        assertEquals("5.", model.getCurrentInput());
        assertEquals("5.", expressionBuilder.toString());
        verify(viewMock, times(1)).updateDisplay("5.");
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
        verify(viewMock, times(1)).updateDisplay("3.141592653589793");
    }
}
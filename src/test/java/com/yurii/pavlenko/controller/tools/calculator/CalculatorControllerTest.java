package com.yurii.pavlenko.controller.tools.calculator;

import com.yurii.pavlenko.model.tools.calculator.CalculatorModel;
import com.yurii.pavlenko.service.tools.calculator.CalculatorService;
import com.yurii.pavlenko.ui.panels.tools.CalculatorDisplay;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javax.swing.JButton;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static org.junit.jupiter.api.Assertions.*;

class CalculatorControllerTest {

    private CalculatorModel model;
    private FakeCalculatorDisplay fakeDisplay;
    private StubCalculatorService stubService;
    private CalculatorController controller;

    private static class FakeCalculatorDisplay implements CalculatorDisplay {
        String lastDisplay = "";
        @Override public void updateDisplay(String text) { lastDisplay = text; }
        @Override public void updateFormulaDisplay(String text) {}
        @Override public void updateMemoryDisplay(String text) {}
        @Override public void registerController(ActionListener c) {}
    }

    private static class StubCalculatorService implements CalculatorService {
        @Override public double calculateExpression(String e, boolean r) { return 0; }
        @Override public double calculateBinary(double a, double b, String op) { return 0; }
        @Override public double calculateUnary(double v, String op, boolean r) { return 0; }
        @Override public void clearMemory() {}
        @Override public double getMemoryValue() { return 0; }
        @Override public void addToMemory(double v) {}
    }

    @BeforeEach
    void setUp() {
        model = new CalculatorModel();
        fakeDisplay = new FakeCalculatorDisplay();
        stubService = new StubCalculatorService();
        controller = new CalculatorController(model, stubService, fakeDisplay);
    }

    @Test
    void shouldClearDisplayWhenClearButtonIsPressed() {
        JButton clearButton = new JButton();
        clearButton.setActionCommand("C");
        ActionEvent event = new ActionEvent(clearButton, ActionEvent.ACTION_PERFORMED, "C");

        controller.actionPerformed(event);

        assertEquals("0", fakeDisplay.lastDisplay);
    }

    @Test
    void shouldProcessDigit() {
        JButton digitButton = new JButton();
        digitButton.setActionCommand("5");
        ActionEvent event = new ActionEvent(digitButton, ActionEvent.ACTION_PERFORMED, "5");

        controller.actionPerformed(event);

        assertEquals("5", fakeDisplay.lastDisplay);
    }
}
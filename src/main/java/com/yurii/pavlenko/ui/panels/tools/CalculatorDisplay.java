package com.yurii.pavlenko.ui.panels.tools;

import java.awt.event.ActionListener;

public interface CalculatorDisplay {
    void updateDisplay(String text);
    void updateFormulaDisplay(String text);
    void updateMemoryDisplay(String text);
    void registerController(ActionListener controller);
}
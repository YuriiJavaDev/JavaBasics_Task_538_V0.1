package com.yurii.pavlenko.controller.tools.calculator;

import com.yurii.pavlenko.model.tools.calculator.CalculatorModel;
import com.yurii.pavlenko.service.tools.calculator.CalculatorService;
import com.yurii.pavlenko.ui.panels.tools.CalculatorPanel;

public class MemoryProcessor {

    private final CalculatorModel model;
    private final CalculatorService service;
    private final CalculatorPanel view;
    private final ResultFormatter formatter;

    public MemoryProcessor(CalculatorModel model, CalculatorService service, CalculatorPanel view, ResultFormatter formatter) {
        this.model = model;
        this.service = service;
        this.view = view;
        this.formatter = formatter;
    }

    public void processMemoryAdd() {
        if ("-".equals(model.getCurrentInput())) {
            return;
        }
        double currentNumber = Double.parseDouble(model.getCurrentInput());
        service.addToMemory(currentNumber);
        view.updateMemoryDisplay(formatter.formatResult(service.getMemoryValue()));
        model.setAwaitingNewInput(true);
    }

    public void processMemoryRecall(StringBuilder expressionBuilder) {
        double memValue = service.getMemoryValue();
        String memString = formatter.formatResult(memValue);
        model.setCurrentInput(memString);
        view.updateDisplay(memString);
        model.setAwaitingNewInput(false);
        expressionBuilder.append(memString);
        view.updateFormulaDisplay(expressionBuilder.toString());
    }

    public void processMemoryClear(StringBuilder expressionBuilder) {
        service.clearMemory();
        view.updateMemoryDisplay("0");
        model.reset();
        expressionBuilder.setLength(0);
        view.updateDisplay(model.getCurrentInput());
        view.updateFormulaDisplay(" ");
    }
}
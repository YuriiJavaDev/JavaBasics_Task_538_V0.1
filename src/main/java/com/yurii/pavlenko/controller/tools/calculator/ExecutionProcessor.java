package com.yurii.pavlenko.controller.tools.calculator;

import com.yurii.pavlenko.model.tools.calculator.CalculatorModel;
import com.yurii.pavlenko.service.tools.calculator.CalculatorService;
import com.yurii.pavlenko.ui.panels.tools.CalculatorPanel;

public class ExecutionProcessor {

    private final CalculatorModel model;
    private final CalculatorService service;
    private final CalculatorPanel view;
    private final ResultFormatter formatter;

    public ExecutionProcessor(CalculatorModel model, CalculatorService service, CalculatorPanel view, ResultFormatter formatter) {
        this.model = model;
        this.service = service;
        this.view = view;
        this.formatter = formatter;
    }

    public void processExpressionCalculate(StringBuilder expressionBuilder) {
        if (expressionBuilder.length() == 0) {
            return;
        }
        int openCount = 0;
        int closeCount = 0;
        String currentContent = expressionBuilder.toString();
        for (char ch : currentContent.toCharArray()) {
            if (ch == '(') openCount++;
            if (ch == ')') closeCount++;
        }
        if (openCount > closeCount) {
            int missingBrackets = openCount - closeCount;
            for (int i = 0; i < missingBrackets; i++) {
                expressionBuilder.append(")");
            }
        }
        String finalExpression = expressionBuilder.toString();
        try {
            double result = service.calculateExpression(finalExpression, model.isRadians());
            String resultString = formatter.formatResult(result);
            model.setCurrentInput(resultString);
            model.setLastResult(result);
            model.setAwaitingNewInput(true);
            view.updateDisplay(resultString);
            view.updateFormulaDisplay(finalExpression + " = " + resultString);
            expressionBuilder.setLength(0);
        } catch (ArithmeticException | IllegalArgumentException ex) {
            view.updateDisplay("Error: " + ex.getMessage());
            view.updateFormulaDisplay(finalExpression + " = Error");
            model.setAwaitingNewInput(true);
            expressionBuilder.setLength(0);
        }
    }
}
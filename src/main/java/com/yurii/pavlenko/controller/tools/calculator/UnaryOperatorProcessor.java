package com.yurii.pavlenko.controller.tools.calculator;

import com.yurii.pavlenko.model.tools.calculator.CalculatorModel;
import com.yurii.pavlenko.ui.panels.tools.CalculatorPanel;

public class UnaryOperatorProcessor {

    private final CalculatorModel model;
    private final CalculatorPanel view;

    public UnaryOperatorProcessor(CalculatorModel model, CalculatorPanel view) {
        this.model = model;
        this.view = view;
    }

    public void processUnaryOperator(String operation, StringBuilder expressionBuilder) {
        if (expressionBuilder.length() == 0 && !"0".equals(model.getCurrentInput())) {
            expressionBuilder.append(model.getCurrentInput());
        }

        String displayToken = operation;

        switch (operation) {
            case "x²" -> expressionBuilder.append("^2");
            case "x³" -> expressionBuilder.append("^3");
            case "n!" -> expressionBuilder.append("!");
            case "%"  -> expressionBuilder.append("/100");
            case "sin", "cos", "tan", "asin", "acos", "atan", "ln", "log", "exp", "sqrt", "cbrt" -> {
                expressionBuilder.append(operation).append("(");
                displayToken = operation + "(";
            }
            case "10^x" -> {
                expressionBuilder.append("10^(");
                displayToken = "10^(";
            }
            case "1/x"  -> {
                expressionBuilder.append("1/(");
                displayToken = "1/(";
            }
            case "abs"  -> {
                expressionBuilder.append("abs(");
                displayToken = "abs(";
            }
            default -> throw new IllegalArgumentException("Unknown unary operation: " + operation);
        }

        view.updateFormulaDisplay(expressionBuilder.toString());
        view.updateDisplay(displayToken);
        model.setCurrentInput(displayToken);
        model.setAwaitingNewInput(true);
    }
}
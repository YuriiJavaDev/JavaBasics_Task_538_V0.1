package com.yurii.pavlenko.controller.tools.calculator;

import com.yurii.pavlenko.model.tools.calculator.CalculatorModel;
import com.yurii.pavlenko.ui.panels.tools.CalculatorPanel;

public class InputProcessor {

    private final CalculatorModel model;
    private final CalculatorPanel view;

    public InputProcessor(CalculatorModel model, CalculatorPanel view) {
        this.model = model;
        this.view = view;
    }

    public void processClear(StringBuilder expressionBuilder) {
        model.reset();
        expressionBuilder.setLength(0);
        view.updateDisplay(model.getCurrentInput());
        view.updateFormulaDisplay(" ");
    }

    public void processDigit(String digit) {
        if (model.isAwaitingNewInput() || "0".equals(model.getCurrentInput())) {
            model.setCurrentInput(digit);
            model.setAwaitingNewInput(false);
        } else {
            model.setCurrentInput(model.getCurrentInput() + digit);
        }
        view.updateDisplay(model.getCurrentInput());
    }

    public void processDot(StringBuilder expressionBuilder) {
        if (model.isAwaitingNewInput()) {
            model.setCurrentInput("0.");
            model.setAwaitingNewInput(false);
        } else if (!model.getCurrentInput().contains(".")) {
            model.setCurrentInput(model.getCurrentInput() + ".");
        }
        view.updateDisplay(model.getCurrentInput());
        expressionBuilder.append(".");
    }

    public void processConstant(double value, StringBuilder expressionBuilder) {
        String constString = String.valueOf(value);
        model.setCurrentInput(constString);
        model.setAwaitingNewInput(false);
        view.updateDisplay(model.getCurrentInput());
        expressionBuilder.append(constString);
        view.updateFormulaDisplay(expressionBuilder.toString());
    }

    public void processBackspace(StringBuilder expressionBuilder) {
        if (expressionBuilder.length() > 0) {
            String currentFormula = expressionBuilder.toString();
            if (currentFormula.endsWith(" mod ")) {
                expressionBuilder.setLength(expressionBuilder.length() - 5);
            } else if (currentFormula.endsWith(" + ") || currentFormula.endsWith(" - ")
                    || currentFormula.endsWith(" * ") || currentFormula.endsWith(" / ")
                    || currentFormula.endsWith(" ^ ")) {
                expressionBuilder.setLength(expressionBuilder.length() - 3);
            } else if (currentFormula.endsWith("asin(") || currentFormula.endsWith("acos(") || currentFormula.endsWith("atan(")
                    || currentFormula.endsWith("sqrt(") || currentFormula.endsWith("cbrt(")) {
                expressionBuilder.setLength(expressionBuilder.length() - 5);
            } else if (currentFormula.endsWith("sin(") || currentFormula.endsWith("cos(") || currentFormula.endsWith("tan(")
                    || currentFormula.endsWith("log(") || currentFormula.endsWith("exp(") || currentFormula.endsWith("abs(")
                    || currentFormula.endsWith("10^(") || currentFormula.endsWith("/100")) {
                expressionBuilder.setLength(expressionBuilder.length() - 4);
            } else if (currentFormula.endsWith("ln(") || currentFormula.endsWith("1/(")) {
                expressionBuilder.setLength(expressionBuilder.length() - 3);
            } else if (currentFormula.endsWith("^2") || currentFormula.endsWith("^3")) {
                expressionBuilder.setLength(expressionBuilder.length() - 2);
            } else {
                expressionBuilder.setLength(expressionBuilder.length() - 1);
            }
            String updatedFormula = expressionBuilder.toString();
            if (updatedFormula.trim().isEmpty() || "0".equals(updatedFormula.trim())) {
                processClear(expressionBuilder);
                return;
            }
            view.updateFormulaDisplay(updatedFormula);
            String trimmedFormula = updatedFormula.trim();
            int lastSpace = trimmedFormula.lastIndexOf(" ");
            String lastToken = (lastSpace != -1) ? trimmedFormula.substring(lastSpace + 1) : trimmedFormula;
            model.setCurrentInput(lastToken);
            view.updateDisplay(lastToken);
        } else {
            String currentInput = model.getCurrentInput();
            if (currentInput.length() > 1 && !"0".equals(currentInput)) {
                String updatedInput = currentInput.substring(0, currentInput.length() - 1);
                if ("-".equals(updatedInput)) {
                    processClear(expressionBuilder);
                    return;
                }
                model.setCurrentInput(updatedInput);
                view.updateDisplay(updatedInput);
            } else {
                processClear(expressionBuilder);
            }
        }
    }
}
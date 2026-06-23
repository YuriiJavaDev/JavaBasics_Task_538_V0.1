package com.yurii.pavlenko.controller.tools.calculator;

import com.yurii.pavlenko.model.tools.calculator.CalculatorModel;
import com.yurii.pavlenko.ui.panels.tools.CalculatorDisplay;
import java.util.stream.Stream;

public class BackspaceProcessor {

    private final CalculatorModel model;
    private final CalculatorDisplay view;
    private final InputProcessor inputProcessor;

    public BackspaceProcessor(CalculatorModel model, CalculatorDisplay view, InputProcessor inputProcessor) {
        this.model = model;
        this.view = view;
        this.inputProcessor = inputProcessor;
    }

    public void processBackspace(StringBuilder exprBuilder) {
        if (exprBuilder.length() > 0) {
            String expr = exprBuilder.toString();

            if (expr.endsWith(" mod ")) {
                exprBuilder.setLength(exprBuilder.length() - 5);
            } else if (Stream.of(" + ", " - ", " * ", " / ", " ^ ").anyMatch(expr::endsWith)) {
                exprBuilder.setLength(exprBuilder.length() - 3);
            } else if (Stream.of("asin(", "acos(", "atan(", "sqrt(", "cbrt(").anyMatch(expr::endsWith)) {
                exprBuilder.setLength(exprBuilder.length() - 5);
            } else if (Stream.of("sin(", "cos(", "tan(", "log(", "exp(", "abs(", "10^(", "/100").anyMatch(expr::endsWith)) {
                exprBuilder.setLength(exprBuilder.length() - 4);
            } else if (Stream.of("ln(", "1/(").anyMatch(expr::endsWith)) {
                exprBuilder.setLength(exprBuilder.length() - 3);
            } else if (Stream.of("^2", "^3").anyMatch(expr::endsWith)) {
                exprBuilder.setLength(exprBuilder.length() - 2);
            } else {
                exprBuilder.setLength(exprBuilder.length() - 1);
            }

            String updatedFormula = exprBuilder.toString();
            if (updatedFormula.trim().isEmpty() || "0".equals(updatedFormula.trim())) {
                inputProcessor.processClear(exprBuilder);
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
                    inputProcessor.processClear(exprBuilder);
                    return;
                }
                model.setCurrentInput(updatedInput);
                view.updateDisplay(updatedInput);
            } else {
                inputProcessor.processClear(exprBuilder);
            }
        }
    }
}
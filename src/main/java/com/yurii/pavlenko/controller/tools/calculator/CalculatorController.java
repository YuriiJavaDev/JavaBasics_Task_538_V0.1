package com.yurii.pavlenko.controller.tools.calculator;

import com.yurii.pavlenko.model.tools.calculator.CalculatorModel;
import com.yurii.pavlenko.service.tools.calculator.CalculatorService;
import com.yurii.pavlenko.ui.panels.tools.CalculatorPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JRadioButton;

public class CalculatorController implements ActionListener {

    private final CalculatorModel model;
    private final CalculatorService service;
    private final CalculatorPanel view;
    private final StringBuilder expressionBuilder = new StringBuilder();

    public CalculatorController(CalculatorModel model, CalculatorService service, CalculatorPanel view) {
        this.model = model;
        this.service = service;
        this.view = view;
        this.view.registerController(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof JRadioButton radioButton) {
            processAngleUnit(radioButton.getActionCommand());
            return;
        }

        if (!(e.getSource() instanceof JButton button)) {
            return;
        }

        String command = button.getActionCommand();

        switch (command) {
            case "C" -> processClear();
            case "Back", "Backspace", "<-" -> processBackspace();
            case "M+" -> processMemoryAdd();
            case "MR" -> processMemoryRecall();
            case "MC" -> processMemoryClear();
            case "." -> processDot();
            case "π" -> processConstant(Math.PI);
            case "e" -> processConstant(Math.E);
            case "Rand" -> processConstant(Math.random());
            case "Ans" -> processConstant(model.getLastResult());
            case "(", ")" -> processBracket(command);
            case "-" -> processMinusOperator();
            case "+", "*", "/", "mod", "x^y" -> processExpressionOperator(command);
            case "Enter" -> processExpressionCalculate();
            default -> {
                if (command.matches("\\d")) {
                    processDigit(command);
                    expressionBuilder.append(command);
                } else {
                    processUnaryOperator(command);
                }
            }
        }
    }

    private void processMinusOperator() {
        String currentFormula = expressionBuilder.toString();

        if (expressionBuilder.length() == 0 || "0".equals(model.getCurrentInput())) {
            model.setCurrentInput("-");
            model.setAwaitingNewInput(false);
            view.updateDisplay("-");
            expressionBuilder.setLength(0);
            expressionBuilder.append("-");
            view.updateFormulaDisplay(expressionBuilder.toString());
        } else if (currentFormula.endsWith("(")) {
            model.setCurrentInput("-");
            model.setAwaitingNewInput(false);
            view.updateDisplay("-");
            expressionBuilder.append("-");
            view.updateFormulaDisplay(expressionBuilder.toString());
        } else {
            processExpressionOperator("-");
        }
    }

    private void processMemoryAdd() {
        if ("-".equals(model.getCurrentInput())) {
            return;
        }

        double currentNumber = Double.parseDouble(model.getCurrentInput());
        service.addToMemory(currentNumber);
        view.updateMemoryDisplay(formatResult(service.getMemoryValue()));
        model.setAwaitingNewInput(true);
    }

    private void processMemoryRecall() {
        double memValue = service.getMemoryValue();
        String memString = formatResult(memValue);

        model.setCurrentInput(memString);
        view.updateDisplay(memString);
        model.setAwaitingNewInput(false);
        expressionBuilder.append(memString);
        view.updateFormulaDisplay(expressionBuilder.toString());
    }

    private void processMemoryClear() {
        service.clearMemory();
        view.updateMemoryDisplay("0");
        model.reset();
        expressionBuilder.setLength(0);
        view.updateDisplay(model.getCurrentInput());
        view.updateFormulaDisplay(" ");
    }

    private void processClear() {
        model.reset();
        expressionBuilder.setLength(0);
        view.updateDisplay(model.getCurrentInput());
        view.updateFormulaDisplay(" ");
    }

    private void processDigit(String digit) {
        if (model.isAwaitingNewInput() || "0".equals(model.getCurrentInput())) {
            model.setCurrentInput(digit);
            model.setAwaitingNewInput(false);
        } else {
            model.setCurrentInput(model.getCurrentInput() + digit);
        }
        view.updateDisplay(model.getCurrentInput());
    }

    private void processDot() {
        if (model.isAwaitingNewInput()) {
            model.setCurrentInput("0.");
            model.setAwaitingNewInput(false);
        } else if (!model.getCurrentInput().contains(".")) {
            model.setCurrentInput(model.getCurrentInput() + ".");
        }
        view.updateDisplay(model.getCurrentInput());
        expressionBuilder.append(".");
    }

    private void processConstant(double value) {
        String constString = String.valueOf(value);
        model.setCurrentInput(constString);
        model.setAwaitingNewInput(false);
        view.updateDisplay(model.getCurrentInput());
        expressionBuilder.append(constString);
        view.updateFormulaDisplay(expressionBuilder.toString());
    }

    private void processUnaryOperator(String operation) {
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

    private void processBracket(String bracket) {
        if (expressionBuilder.length() == 0 && !"0".equals(model.getCurrentInput())) {
            expressionBuilder.append(model.getCurrentInput());
        }

        expressionBuilder.append(bracket);
        view.updateDisplay(bracket);
        model.setCurrentInput(bracket);
        view.updateFormulaDisplay(expressionBuilder.toString());
        model.setAwaitingNewInput(true);
    }

    private void processExpressionOperator(String operator) {
        if (expressionBuilder.length() == 0) {
            expressionBuilder.append(model.getCurrentInput());
        }

        String parserOperator = operator.equals("x^y") ? "^" : operator;
        expressionBuilder.append(" ").append(parserOperator).append(" ");

        view.updateFormulaDisplay(expressionBuilder.toString());
        model.setAwaitingNewInput(true);
    }

    private void processExpressionCalculate() {
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
            String resultString = formatResult(result);
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

    private void processBackspace() {
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
                processClear();
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
                    processClear();
                    return;
                }

                model.setCurrentInput(updatedInput);
                view.updateDisplay(updatedInput);
            } else {
                processClear();
                return;
            }
        }
    }

    private String formatResult(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return String.valueOf(value);
        }

        if (Math.abs(value) > 1e14 || (Math.abs(value) < 1e-12 && value != 0)) {
            return String.valueOf(value);
        }

        double scaled = Math.round(value * 1e12) / 1e12;
        if (scaled % 1 == 0) {
            return String.valueOf((long) scaled);
        }
        return String.valueOf(scaled);
    }

    private void processAngleUnit(String command) {
        if ("Rad".equals(command)) {
            model.setRadians(true);
        } else {
            model.setRadians(false);
        }
    }
}
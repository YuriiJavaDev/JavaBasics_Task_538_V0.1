package main.com.yurii.pavlenko.controller.tools.calculator;

import main.com.yurii.pavlenko.model.tools.calculator.CalculatorModel;
import main.com.yurii.pavlenko.service.tools.calculator.CalculatorService;
import main.com.yurii.pavlenko.ui.panels.tools.CalculatorPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JRadioButton;

/**
 * Controller component coordinating workflow between Calculator Model, Service, and UI Panel.
 * Centralizes UI updates into unified termination routines.
 */
public class CalculatorController implements ActionListener {

    private final CalculatorModel model;
    private final CalculatorService service;
    private final CalculatorPanel view;

    public CalculatorController(CalculatorModel model, CalculatorService service, CalculatorPanel view) {
        this.model = model;
        this.service = service;
        this.view = view;

        this.view.registerController(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // РЕФАКТОРИНГ: Добавляем обработку кликов по радиокнопкам Deg/Rad
        if (e.getSource() instanceof JRadioButton radioButton) {
            processAngleUnit(radioButton.getActionCommand());
            return;
        }

        if (!(e.getSource() instanceof JButton button)) {
            return;
        }

        String command = button.getActionCommand();

        try {
            switch (command) {
                case "C" -> processClear();

                case "M+" -> processMemoryAdd();
                case "MR" -> processMemoryRecall();
                case "MC" -> processMemoryClear();

                case "." -> processDot();
                case "π" -> processConstant(Math.PI);
                case "e" -> processConstant(Math.E);
                case "Rand" -> processConstant(Math.random());
                case "Ans" -> processConstant(model.getLastResult());

                // Добавлены новые инженерные кейсы, если они требуют специфики
                case "+", "*", "/", "mod", "x^y" -> processBinaryOperator(command);
                case "-" -> processMinusOperator();
                case "Enter" -> processCalculate();

                default -> {
                    if (command.matches("\\d")) {
                        processDigit(command);
                    } else if (command.equals("%")) {
                        processUnaryOperator(command);
                    } else if (!command.trim().isEmpty() && !command.equals("(") && !command.equals(")")) {
                        processUnaryOperator(command);
                    }
                }
            }
        } catch (ArithmeticException | IllegalArgumentException ex) {
            view.updateDisplay("Error: " + ex.getMessage());
            model.setAwaitingNewInput(true);
        }
    }

    private void processMinusOperator() {
        if ("0".equals(model.getCurrentInput())) {
            model.setCurrentInput("-");
            model.setAwaitingNewInput(false);
            view.updateDisplay("-");
        } else {
            processBinaryOperator("-");
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
    }

    private void processMemoryClear() {
        service.clearMemory();
        view.updateMemoryDisplay("0");
        model.reset();
        view.updateDisplay(model.getCurrentInput());
        view.updateFormulaDisplay(" ");
    }

    private void processClear() {
        model.reset();
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
    }

    private void processConstant(double value) {
        model.setCurrentInput(String.valueOf(value));
        model.setAwaitingNewInput(false);
        view.updateDisplay(model.getCurrentInput());
    }

    private void processBinaryOperator(String operator) {
        double currentNumber = Double.parseDouble(model.getCurrentInput());
        model.setFirstOperand(currentNumber);
        model.setActiveOperator(operator);
        model.setAwaitingNewInput(true);

        view.updateFormulaDisplay(formatResult(currentNumber) + " " + operator);
    }

    private void processUnaryOperator(String operation) {
        double currentNumber = Double.parseDouble(model.getCurrentInput());
        double result = service.calculateUnary(currentNumber, operation, model.isRadians());

        // РЕФАКТОРИНГ: Добавляем суффикс угловых мер СТРОГО для тригонометрии
        String unit = "";
        if (operation.equals("sin") || operation.equals("cos") || operation.equals("tan")
                || operation.equals("asin") || operation.equals("acos") || operation.equals("atan")
                || operation.equals("sinh") || operation.equals("cosh")) {
            unit = model.isRadians() ? " rad" : " deg";
        }

        String formulaString = operation + "(" + formatResult(currentNumber) + unit + ") = ";
        finalizeCalculation(result, formulaString);
    }

    private void processCalculate() {
        if (model.getActiveOperator().isEmpty()) {
            return;
        }
        double secondOperand = Double.parseDouble(model.getCurrentInput());
        double firstOperand = model.getFirstOperand();
        String operator = model.getActiveOperator();

        double result = service.calculateBinary(firstOperand, secondOperand, operator);
        model.setActiveOperator("");

        String formulaString = formatResult(firstOperand) + " " + operator + " " + formatResult(secondOperand) + " = ";

        finalizeCalculation(result, formulaString);
    }

    private void finalizeCalculation(double result, String baseFormula) {
        String resultString = formatResult(result);

        model.setCurrentInput(resultString);
        model.setLastResult(result);
        model.setAwaitingNewInput(true);

        view.updateDisplay(resultString);
        view.updateFormulaDisplay(baseFormula + resultString);
    }

    private String formatResult(double value) {
        return (value % 1 == 0) ? String.valueOf((long) value) : String.valueOf(value);
    }

    private void processAngleUnit(String command) {
        if ("Rad".equals(command)) {
            model.setRadians(true);
        } else {
            model.setRadians(false);
        }
    }
}
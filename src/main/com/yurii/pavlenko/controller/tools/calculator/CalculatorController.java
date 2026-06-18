package main.com.yurii.pavlenko.controller.tools.calculator;

import main.com.yurii.pavlenko.model.tools.calculator.CalculatorModel;
import main.com.yurii.pavlenko.service.tools.calculator.CalculatorService;
import main.com.yurii.pavlenko.ui.panels.tools.CalculatorPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

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
                case "Deg", "Rad" -> processAngleUnit(command);
                case "π" -> processConstant(Math.PI);
                case "e" -> processConstant(Math.E);
                case "Rand" -> processConstant(Math.random());
                case "Ans" -> processConstant(model.getLastResult());

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

        // Лаконично отправляем промежуточный этап в мини-табло: "10 mod"
        view.updateFormulaDisplay(formatResult(currentNumber) + " " + operator);
    }

    private void processUnaryOperator(String operation) {
        double currentNumber = Double.parseDouble(model.getCurrentInput());
        double result = service.calculateUnary(currentNumber, operation, model.isRadians());

        // Определяем суффикс единицы измерения для тригонометрии
        String unit = model.isRadians() ? " rad" : " deg";
        // Собираем левую часть формулы: "sin(90) = "
        String formulaString = operation + "(" + formatResult(currentNumber) + unit + ") = ";
        // Передаем управление в центральный метод финализации
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

        // Строим левую часть для бинарного вычисления: "10 mod 3 = "
        String formulaString = formatResult(firstOperand) + " " + operator + " " + formatResult(secondOperand) + " = ";

        finalizeCalculation(result, formulaString);
    }

    /**
     * Centralized termination method for all arithmetic execution workflows.
     * Synchronizes model state, main numeric display, and contextual formula tracking.
     */
    private void finalizeCalculation(double result, String baseFormula) {
        String resultString = formatResult(result);

        // Обновляем состояние модели
        model.setCurrentInput(resultString);
        model.setLastResult(result);
        model.setAwaitingNewInput(true);

        // Синхронно выводим данные на UI-компоненты
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
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
 * Manages dual-state calculations: sequential binary steps or continuous string expression parsing.
 */
public class CalculatorController implements ActionListener {

    private final CalculatorModel model;
    private final CalculatorService service;
    private final CalculatorPanel view;

    // Переменные для поддержки скобок и построчного ввода выражений
    private final StringBuilder expressionBuilder = new StringBuilder();
    private boolean isExpressionMode = false;

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

                // Перехватываем управление скобками
                case "(", ")" -> processBracket(command);

                case "+", "*", "/", "mod", "x^y" -> {
                    if (isExpressionMode) {
                        processExpressionOperator(command);
                    } else {
                        processBinaryOperator(command);
                    }
                }
                case "-" -> {
                    if (isExpressionMode) {
                        processExpressionOperator("-");
                    } else {
                        processMinusOperator();
                    }
                }

                case "Enter" -> {
                    if (isExpressionMode) {
                        processExpressionCalculate();
                    } else {
                        processCalculate();
                    }
                }

                default -> {
                    if (command.matches("\\d")) {
                        processDigit(command);
                        // Если мы строим выражение, дублируем вводимую цифру в строку парсера
                        if (isExpressionMode) {
                            expressionBuilder.append(command);
                        }
                    } else if (command.equals("%")) {
                        processUnaryOperator(command);
                    } else {
                        processUnaryOperator(command);
                    }
                }
            }
        } catch (ArithmeticException | IllegalArgumentException ex) {
            view.updateDisplay("Error: " + ex.getMessage());
            model.setAwaitingNewInput(true);
            expressionBuilder.setLength(0);
            isExpressionMode = false;
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

        if (isExpressionMode) {
            expressionBuilder.append(memString);
            view.updateFormulaDisplay(expressionBuilder.toString());
        }
    }

    private void processMemoryClear() {
        service.clearMemory();
        view.updateMemoryDisplay("0");
        model.reset();
        expressionBuilder.setLength(0);
        isExpressionMode = false;
        view.updateDisplay(model.getCurrentInput());
        view.updateFormulaDisplay(" ");
    }

    private void processClear() {
        model.reset();
        expressionBuilder.setLength(0);
        isExpressionMode = false;
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
        if (isExpressionMode) {
            expressionBuilder.append(".");
        }
    }

    private void processConstant(double value) {
        String constString = String.valueOf(value);
        model.setCurrentInput(constString);
        model.setAwaitingNewInput(false);
        view.updateDisplay(model.getCurrentInput());
        if (isExpressionMode) {
            expressionBuilder.append(constString);
            view.updateFormulaDisplay(expressionBuilder.toString());
        }
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

        String unit = "";
        if (operation.equals("sin") || operation.equals("cos") || operation.equals("tan")
                || operation.equals("asin") || operation.equals("acos") || operation.equals("atan")) {
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

    // --- МЕТОДЫ ДЛЯ РАБОТЫ СО СКОБКАМИ (EXPRESSION MODE) ---

    private void processBracket(String bracket) {
        if (!isExpressionMode) {
            isExpressionMode = true;
            expressionBuilder.setLength(0);

            // ПРАВКА: Если до скобки уже был нажат оператор (например, "2 *"),
            // мы берём первый операнд и сам оператор прямо из модели!
            if (!model.getActiveOperator().isEmpty()) {
                expressionBuilder.append(formatResult(model.getFirstOperand()))
                        .append(" ")
                        .append(model.getActiveOperator())
                        .append(" ");
                // Сбрасываем старый бинарный оператор модели, чтобы он не двоился при Enter
                model.setActiveOperator("");
            }
            // Иначе, если операции не было, но на табло введено число (не дефолтный ноль)
            else if (!"0".equals(model.getCurrentInput()) && !model.isAwaitingNewInput()) {
                expressionBuilder.append(model.getCurrentInput());
            }
        }

        expressionBuilder.append(bracket);
        view.updateDisplay(bracket); // Показываем скобку на основном дисплее
        view.updateFormulaDisplay(expressionBuilder.toString());
        model.setAwaitingNewInput(true);
    }

    private void processExpressionOperator(String operator) {
        // Конвертируем UI оператор степени x^y в системный символ '^' для парсера
        String parserOperator = operator.equals("x^y") ? "^" : operator;

        // Добавляем красивую разрядку пробелами для отображения в формуле
        expressionBuilder.append(" ").append(parserOperator).append(" ");
        view.updateFormulaDisplay(expressionBuilder.toString());
        model.setAwaitingNewInput(true);
    }

    private void processExpressionCalculate() {
        String finalExpression = expressionBuilder.toString();

        // Отправляем собранную строку в наш новый метод сервиса
        double result = service.calculateExpression(finalExpression);

        String resultString = formatResult(result);
        model.setCurrentInput(resultString);
        model.setLastResult(result);
        model.setAwaitingNewInput(true);

        view.updateDisplay(resultString);
        view.updateFormulaDisplay(finalExpression + " = " + resultString);

        // Гасим режим выражения и чистим буфер до следующей скобки
        expressionBuilder.setLength(0);
        isExpressionMode = false;
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
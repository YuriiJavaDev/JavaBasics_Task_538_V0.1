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
 * Manages dynamic string expression parsing using a continuous sequence-building workflow.
 */
public class CalculatorController implements ActionListener {

    private final CalculatorModel model;
    private final CalculatorService service;
    private final CalculatorPanel view;

    // Единый буфер для поддержки построчного ввода выражений
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

        try {
            switch (command) {
                case "C" -> processClear();
                case "Back", "Backspace", "<-" -> processBackspace(); // Поддержка всех вариантов команд для удаления

                case "M+" -> processMemoryAdd();
                case "MR" -> processMemoryRecall();
                case "MC" -> processMemoryClear();

                case "." -> processDot();
                case "π" -> processConstant(Math.PI);
                case "e" -> processConstant(Math.E);
                case "Rand" -> processConstant(Math.random());
                case "Ans" -> processConstant(model.getLastResult());

                case "(", ")" -> processBracket(command);

                // Выносим минус в собственный обработчик для поддержки унарных чисел (-5)
                case "-" -> processMinusOperator();

                // Остальные бинарные операторы идут через построчное накопление
                case "+", "*", "/", "mod", "x^y" -> processExpressionOperator(command);

                // Enter ВСЕГДА вызывает парсер строки
                case "Enter" -> processExpressionCalculate();

                default -> {
                    if (command.matches("\\d")) {
                        processDigit(command);
                        // Всегда пишем цифру в буфер парсера
                        expressionBuilder.append(command);
                    } else {
                        processUnaryOperator(command);
                    }
                }
            }
        } catch (ArithmeticException | IllegalArgumentException ex) {
            view.updateDisplay("Error: " + ex.getMessage());
            model.setAwaitingNewInput(true);
            expressionBuilder.setLength(0);
        }
    }

    private void processMinusOperator() {
        // Унарный минус на старте
        if (expressionBuilder.length() == 0 || "0".equals(model.getCurrentInput())) {
            model.setCurrentInput("-");
            model.setAwaitingNewInput(false);
            view.updateDisplay("-");

            expressionBuilder.setLength(0);
            expressionBuilder.append("-");
            view.updateFormulaDisplay(expressionBuilder.toString());
        }
        // Унарный минус сразу после открывающей скобки, например: (-5
        else if (expressionBuilder.toString().endsWith("(")) {
            model.setCurrentInput("-");
            model.setAwaitingNewInput(false);
            view.updateDisplay("-");

            expressionBuilder.append("-");
            view.updateFormulaDisplay(expressionBuilder.toString());
        }
        // Бинарный минус (вычитание)
        else {
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

        // Всегда добавляем значение памяти в выражение
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

        // Точка в строке формулы
        expressionBuilder.append(".");
    }

    private void processConstant(double value) {
        String constString = String.valueOf(value);
        model.setCurrentInput(constString);
        model.setAwaitingNewInput(false);
        view.updateDisplay(model.getCurrentInput());

        // Константы склеиваются с буфером выражения
        expressionBuilder.append(constString);
        view.updateFormulaDisplay(expressionBuilder.toString());
    }

    private void processUnaryOperator(String operation) {
        // Если буфер пуст, но на экране уже введено число, подхватываем его
        if (expressionBuilder.length() == 0 && !"0".equals(model.getCurrentInput())) {
            expressionBuilder.append(model.getCurrentInput());
        }

        String displayToken = operation; // То, что пойдет на главное табло

        switch (operation) {
            // 1. ПОСТФИКСНЫЕ ОПЕРАЦИИ (дописываются после текущего числа/выражения)
            case "x²" -> expressionBuilder.append("^2");
            case "x³" -> expressionBuilder.append("^3");
            case "n!" -> expressionBuilder.append("!");
            case "%"  -> expressionBuilder.append("/100");

            // 2. ПРЕФИКСНЫЕ ФУНКЦИИ (оборачивают число или открывают зону для ввода)
            case "sin", "cos", "tan", "asin", "acos", "atan", "ln", "log", "exp", "sqrt", "cbrt" -> {
                expressionBuilder.append(operation).append("(");
                displayToken = operation + "("; // РЕФАКТОРИНГ: Выводим на табло со скобкой
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

        // Обновляем верхнее табло текущим состоянием формулы
        view.updateFormulaDisplay(expressionBuilder.toString());

        // РЕФАКТОРИНГ: Показываем операцию со скобкой на главном экране и ждем ввода аргумента
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

        String finalExpression = expressionBuilder.toString();

        // РЕФАКТОРИНГ: Автоматическое исправление/закрытие скобок перед вычислением
        int openCount = 0;
        int closeCount = 0;
        for (char ch : finalExpression.toCharArray()) {
            if (ch == '(') openCount++;
            if (ch == ')') closeCount++;
        }

        if (openCount > closeCount) {
            int missingBrackets = openCount - closeCount;
            for (int i = 0; i < missingBrackets; i++) {
                expressionBuilder.append(")");
            }
            finalExpression = expressionBuilder.toString(); // Обновляем выражение для парсера
        }

        double result = service.calculateExpression(finalExpression, model.isRadians());

        String resultString = formatResult(result);
        model.setCurrentInput(resultString);
        model.setLastResult(result);
        model.setAwaitingNewInput(true);

        view.updateDisplay(resultString);
        view.updateFormulaDisplay(finalExpression + " = " + resultString);

        expressionBuilder.setLength(0);
    }

    private void finalizeCalculation(double result, String baseFormula) {
        String resultString = formatResult(result);

        model.setCurrentInput(resultString);
        model.setLastResult(result);
        model.setAwaitingNewInput(true);

        view.updateDisplay(resultString);
        view.updateFormulaDisplay(baseFormula + resultString);
    }

    private void processBackspace() {
        // Если в буфере выражения что-то есть, работаем с ним напрямую
        if (expressionBuilder.length() > 0) {
            String currentFormula = expressionBuilder.toString();

            // Пошаговый разбор окончания строки для умного удаления
            if (currentFormula.endsWith(" ")) {
                // Оператор с пробелами (например, " + ")
                expressionBuilder.setLength(expressionBuilder.length() - 3);
            } else if (currentFormula.endsWith("sin(") || currentFormula.endsWith("cos(") || currentFormula.endsWith("tan(")
                    || currentFormula.endsWith("log(") || currentFormula.endsWith("exp(") || currentFormula.endsWith("abs(")) {
                // Функции из 4-х символов
                expressionBuilder.setLength(expressionBuilder.length() - 4);
            } else if (currentFormula.endsWith("asin(") || currentFormula.endsWith("acos(") || currentFormula.endsWith("atan(")
                    || currentFormula.endsWith("sqrt(") || currentFormula.endsWith("cbrt(") || currentFormula.endsWith("10^(")) {
                // Функции из 5 символов
                expressionBuilder.setLength(expressionBuilder.length() - 5);
            } else if (currentFormula.endsWith("1/(")) {
                expressionBuilder.setLength(expressionBuilder.length() - 3);
            } else {
                // Обычная цифра, точка или одиночная скобка
                expressionBuilder.setLength(expressionBuilder.length() - 1);
            }

            // Обновляем верхний лейбл формулы
            String updatedFormula = expressionBuilder.toString();
            view.updateFormulaDisplay(updatedFormula.isEmpty() ? " " : updatedFormula);

            // Синхронизируем нижнее табло
            String trimmedFormula = updatedFormula.trim();
            if (trimmedFormula.isEmpty()) {
                model.setCurrentInput("0");
                view.updateDisplay("0");
            } else {
                int lastSpace = trimmedFormula.lastIndexOf(" ");
                String lastToken = (lastSpace != -1) ? trimmedFormula.substring(lastSpace + 1) : trimmedFormula;

                model.setCurrentInput(lastToken);
                view.updateDisplay(lastToken);
            }
        } else {
            // Если буфер выражения пуст, но на табло длинное число
            String currentInput = model.getCurrentInput();
            if (currentInput.length() > 1 && !"0".equals(currentInput)) {
                String updatedInput = currentInput.substring(0, currentInput.length() - 1);

                if ("-".equals(updatedInput)) {
                    updatedInput = "0";
                }

                model.setCurrentInput(updatedInput);
                view.updateDisplay(updatedInput);
            } else {
                model.setCurrentInput("0");
                view.updateDisplay("0");
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
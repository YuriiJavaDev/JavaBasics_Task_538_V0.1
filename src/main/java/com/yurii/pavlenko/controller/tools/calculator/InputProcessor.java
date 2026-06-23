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

    public void processDigitWithResetCheck(String digit, StringBuilder expressionBuilder) {
        String currentFormula = expressionBuilder.toString().trim();

        // Проверяем, оканчивается ли строка на константу (π, e, или букву 'd' от Rand/Ans)
        boolean isConstant = currentFormula.endsWith("π")
                || currentFormula.endsWith("e")
                || currentFormula.endsWith("d");

        // Если калькулятор ждёт новый ввод ИЛИ в лейбе сейчас горит чистая константа
        if (model.isAwaitingNewInput() || (isConstant && !currentFormula.contains(" "))) {

            // Символы, после которых стирать лейбу НЕЛЬЗЯ (операторы и открытые скобки)
            boolean endsWithForbidden = currentFormula.endsWith("(")
                    || currentFormula.endsWith("+") || currentFormula.endsWith("-")
                    || currentFormula.endsWith("*") || currentFormula.endsWith("/")
                    || currentFormula.endsWith("^") || currentFormula.endsWith("mod");

            // Стираем лейбу, если это одиночное число/константа без операторов
            if ((!currentFormula.contains(" ") || isConstant) && !endsWithForbidden) {
                expressionBuilder.setLength(0);
                // Если мы затираем константу, принудительно переводим табло в режим чистого листа
                if (isConstant) {
                    model.setCurrentInput("0");
                    model.setAwaitingNewInput(true);
                }
            }

            model.setCalculatedOrMemory(false);
        }

        // Стандартный ввод цифры (твоя оригинальная логика из processDigit)
        if (model.isAwaitingNewInput() || "0".equals(model.getCurrentInput())) {
            model.setCurrentInput(digit);
            model.setAwaitingNewInput(false);
        } else {
            model.setCurrentInput(model.getCurrentInput() + digit);
        }
        view.updateDisplay(model.getCurrentInput());

        // Синхронизируем лейбу с историей выражения
        if (expressionBuilder.length() == 0) {
            expressionBuilder.append(digit);
        } else {
            String formula = expressionBuilder.toString();
            if (formula.endsWith(" ")) {
                expressionBuilder.append(digit);
            } else {
                expressionBuilder.append(digit);
            }
        }
        view.updateFormulaDisplay(expressionBuilder.toString());
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
        model.setAwaitingNewInput(true);
    }
}
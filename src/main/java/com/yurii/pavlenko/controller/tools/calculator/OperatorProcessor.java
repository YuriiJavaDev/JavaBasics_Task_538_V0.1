package com.yurii.pavlenko.controller.tools.calculator;

import com.yurii.pavlenko.model.tools.calculator.CalculatorModel;
import com.yurii.pavlenko.ui.panels.tools.CalculatorDisplay;

public class OperatorProcessor {

    private final CalculatorModel model;
    private final CalculatorDisplay view;

    public OperatorProcessor(CalculatorModel model, CalculatorDisplay view) {
        this.model = model;
        this.view = view;
    }

    public void processMinusOperator(StringBuilder expressionBuilder) {
        // Как только нажали оператор, число из MR больше не является "активным" для затирания цифрой
        model.setCalculatedOrMemory(false);

        String currentFormula = expressionBuilder.toString();

        if (currentFormula.endsWith("(")) {
            model.setCurrentInput("-");
            model.setAwaitingNewInput(false);
            view.updateDisplay("-");
            expressionBuilder.append("-");
            view.updateFormulaDisplay(expressionBuilder.toString());
        } else if (expressionBuilder.length() == 0 && "0".equals(model.getCurrentInput())) {
            model.setCurrentInput("-");
            model.setAwaitingNewInput(false);
            view.updateDisplay("-");
            expressionBuilder.setLength(0);
            expressionBuilder.append("-");
            view.updateFormulaDisplay(expressionBuilder.toString());
        } else {
            // Здесь вызовется метод ниже, но флаг мы уже сбросили
            processExpressionOperator("-", expressionBuilder);
        }
    }

    public void processExpressionOperator(String operator, StringBuilder expressionBuilder) {
        // Принудительно тушим флаг MR при выборе любого бинарного оператора (+, *, /, mod, x^y)
        model.setCalculatedOrMemory(false);

        if (expressionBuilder.length() == 0) {
            expressionBuilder.append(model.getCurrentInput());
        }

        String parserOperator = operator.equals("x^y") ? "^" : operator;
        expressionBuilder.append(" ").append(parserOperator).append(" ");

        view.updateFormulaDisplay(expressionBuilder.toString());
        model.setAwaitingNewInput(true);
    }

    public void processBracket(String bracket, StringBuilder expressionBuilder) {
        // Если после MR открывают скобку, это тоже ломает монолитность числа из памяти
        model.setCalculatedOrMemory(false);

        if (expressionBuilder.length() == 0 && !"0".equals(model.getCurrentInput())) {
            expressionBuilder.append(model.getCurrentInput());
        }

        expressionBuilder.append(bracket);
        view.updateDisplay(bracket);
        model.setCurrentInput(bracket);
        view.updateFormulaDisplay(expressionBuilder.toString());
        model.setAwaitingNewInput(true);
    }
}
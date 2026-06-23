package com.yurii.pavlenko.controller.tools.calculator;

import com.yurii.pavlenko.model.tools.calculator.CalculatorModel;
import com.yurii.pavlenko.service.tools.calculator.CalculatorService;
import com.yurii.pavlenko.ui.panels.tools.CalculatorPanel;

public class MemoryProcessor {

    private final CalculatorModel model;
    private final CalculatorService service;
    private final CalculatorPanel view;
    private final ResultFormatter formatter;

    public MemoryProcessor(CalculatorModel model, CalculatorService service, CalculatorPanel view, ResultFormatter formatter) {
        this.model = model;
        this.service = service;
        this.view = view;
        this.formatter = formatter;
    }

    public void processMemoryAdd() {
        if ("-".equals(model.getCurrentInput())) {
            return;
        }
        double currentNumber = Double.parseDouble(model.getCurrentInput());
        service.addToMemory(currentNumber);
        view.updateMemoryDisplay(formatter.formatResult(service.getMemoryValue()));
        model.setAwaitingNewInput(true);
    }

//    public void processMemoryRecall(StringBuilder expressionBuilder) {
//        double memValue = service.getMemoryValue();
//        String memString = formatter.formatResult(memValue);
//        model.setCurrentInput(memString);
//        view.updateDisplay(memString);
//        model.setAwaitingNewInput(false);
//        expressionBuilder.append(memString);
//        view.updateFormulaDisplay(expressionBuilder.toString());
//    }

//    public void processMemoryRecall(StringBuilder expressionBuilder) {
//        double memValue = service.getMemoryValue();
//        String memString = formatter.formatResult(memValue);
//
//        // ПРАВИЛО: Избавляемся от дублирования при повторных нажатиях подряд
//        // Если на табло уже горит то же самое число из памяти, и мы кликаем MR опять — ничего не делаем
//        if (memString.equals(model.getCurrentInput()) && !model.isAwaitingNewInput()) {
//            return;
//        }
//
//        // Если калькулятор пуст или ждёт новый ввод (например, после знака "+" или функции "sin(")
//        if (expressionBuilder.length() == 0 || model.isAwaitingNewInput()) {
//            expressionBuilder.append(memString);
//        } else {
//            // Если пользователь вводит MR взамен другого числа (или перезаписывает ввод)
//            String currentFormula = expressionBuilder.toString().trim();
//            int lastSpace = currentFormula.lastIndexOf(" ");
//
//            if (lastSpace != -1) {
//                // Отрезаем старый хвост после пробела и вставляем число из памяти
//                expressionBuilder.setLength(lastSpace + 1);
//                expressionBuilder.append(memString);
//            } else {
//                // Если пробелов нет (было просто введено одно число), полностью заменяем его
//                expressionBuilder.setLength(0);
//                expressionBuilder.append(memString);
//            }
//        }
//
//        model.setCurrentInput(memString);
//        view.updateDisplay(memString);
//
//        // Ставим false, чтобы калькулятор понимал: число ЗАФИКСИРОВАНО.
//        // Теперь, если нажать цифру — она затрёт это число, а если оператор (+, -, sin) — он применится к нему.
//        model.setAwaitingNewInput(false);
//
//        view.updateFormulaDisplay(expressionBuilder.toString());
//    }

    public void processMemoryRecall(StringBuilder expressionBuilder) {
        double memValue = service.getMemoryValue();
        String memString = formatter.formatResult(memValue);

        // Если мы кликаем MR повторно подряд, и число в табло совпадает — ничего не делаем
        if (memString.equals(model.getCurrentInput()) && model.isAwaitingNewInput()) {
            return;
        }

        String currentFormula = expressionBuilder.toString().trim();

        // ПРОВЕРКА: оканчивается ли строка на оператор?
        boolean endsWithOperator = currentFormula.endsWith("+") || currentFormula.endsWith("-")
                || currentFormula.endsWith("*") || currentFormula.endsWith("/")
                || currentFormula.endsWith("^") || currentFormula.endsWith("mod")
                || currentFormula.endsWith("(");

        if (expressionBuilder.length() == 0 || endsWithOperator) {
            // Если в истории пусто или там стоит знак операции (например, "15 -"),
            // то число из памяти — это законное продолжение, просто добавляем его в хвост
            expressionBuilder.append(memString);
        } else {
            // Если знаков операции на конце нет (пользователь вводил число),
            // то заменяем последнее введенное число на значение из MR
            int lastSpace = currentFormula.lastIndexOf(" ");

            if (lastSpace != -1) {
                expressionBuilder.setLength(lastSpace + 1);
                expressionBuilder.append(memString);
            } else {
                expressionBuilder.setLength(0);
                expressionBuilder.append(memString);
            }
        }

        model.setCurrentInput(memString);
        view.updateDisplay(memString);
        model.setAwaitingNewInput(true);
        model.setCalculatedOrMemory(true);

        view.updateFormulaDisplay(expressionBuilder.toString());
    }

    public void processMemoryClear(StringBuilder expressionBuilder) {
        service.clearMemory();
        view.updateMemoryDisplay("0");
        model.reset();
        expressionBuilder.setLength(0);
        view.updateDisplay(model.getCurrentInput());
        view.updateFormulaDisplay(" ");
    }
}
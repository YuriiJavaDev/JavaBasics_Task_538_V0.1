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
    private final CalculatorPanel view;
    private final StringBuilder expressionBuilder = new StringBuilder();

    private final InputProcessor inputProcessor;
    private final BackspaceProcessor backspaceProcessor;
    private final MemoryProcessor memoryProcessor;
    private final OperatorProcessor operatorProcessor;
    private final UnaryOperatorProcessor unaryOperatorProcessor;
    private final ExecutionProcessor executionProcessor;

    public CalculatorController(CalculatorModel model, CalculatorService service, CalculatorPanel view) {
        this.model = model;
        this.view = view;

        ResultFormatter formatter = new ResultFormatter();
        this.inputProcessor = new InputProcessor(model, view);
        this.backspaceProcessor = new BackspaceProcessor(model, view, inputProcessor);
        this.memoryProcessor = new MemoryProcessor(model, service, view, formatter);
        this.operatorProcessor = new OperatorProcessor(model, view);
        this.unaryOperatorProcessor = new UnaryOperatorProcessor(model, view);
        this.executionProcessor = new ExecutionProcessor(model, service, view, formatter);

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
            case "C" -> inputProcessor.processClear(expressionBuilder);
            case "Back", "Backspace", "<-" -> backspaceProcessor.processBackspace(expressionBuilder);
            case "M+" -> memoryProcessor.processMemoryAdd();
            case "MR" -> memoryProcessor.processMemoryRecall(expressionBuilder);
            case "MC" -> memoryProcessor.processMemoryClear(expressionBuilder);
            case "." -> inputProcessor.processDot(expressionBuilder);
            case "π" -> inputProcessor.processConstant(Math.PI, expressionBuilder);
            case "e" -> inputProcessor.processConstant(Math.E, expressionBuilder);
            case "Rand" -> inputProcessor.processConstant(Math.random(), expressionBuilder);
            case "Ans" -> inputProcessor.processConstant(model.getLastResult(), expressionBuilder);
            case "(", ")" -> operatorProcessor.processBracket(command, expressionBuilder);
            case "-" -> operatorProcessor.processMinusOperator(expressionBuilder);
            case "+", "*", "/", "mod", "x^y" -> operatorProcessor.processExpressionOperator(command, expressionBuilder);
            case "Enter" -> executionProcessor.processExpressionCalculate(expressionBuilder);
            default -> {
                if (command.matches("\\d")) {
                    String currentFormula = expressionBuilder.toString().trim();

                    // Проверяем, оканчивается ли строка на константу (π, e, или букву 'd' от Rand/Ans)
                    boolean isConstant = currentFormula.endsWith("π")
                            || currentFormula.endsWith("e")
                            || currentFormula.endsWith("d")  // для Rand
                            // если Ans выводится как слово, можно добавить: || currentFormula.endsWith("s")
                            ;

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

                    // Стандартный ввод цифры
                    inputProcessor.processDigit(command);

                    // Синхронизируем лейбу
                    if (expressionBuilder.length() == 0) {
                        expressionBuilder.append(command);
                    } else {
                        String formula = expressionBuilder.toString();
                        if (formula.endsWith(" ")) {
                            expressionBuilder.append(command);
                        } else {
                            expressionBuilder.append(command);
                        }
                    }
                    view.updateFormulaDisplay(expressionBuilder.toString());
                } else {
                    unaryOperatorProcessor.processUnaryOperator(command, expressionBuilder);
                }
            }
        }
    }

    private void processAngleUnit(String command) {
        if ("Rad".equals(command)) {
            model.setRadians(true);
        } else {
            model.setRadians(false);
        }
    }
}
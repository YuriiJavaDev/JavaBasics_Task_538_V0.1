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
                    // ПРАВИЛО: Если число на табло из MR — полностью стираем лейбу и табло
                    if (model.isCalculatedOrMemory()) {
                        expressionBuilder.setLength(0); // Стираем лейбу
                        model.setCurrentInput("0");     // Сбрасываем модель табло для inputProcessor
                        model.setAwaitingNewInput(true);
                        model.setCalculatedOrMemory(false); // Сбрасываем маркер MR
                    }

                    // Стандартный ввод (он сам разберётся: сделать "7" -> "78" или начать заново)
                    inputProcessor.processDigit(command);

                    // Синхронизируем лейбу с тем, что ввёл inputProcessor
                    if (expressionBuilder.length() == 0) {
                        expressionBuilder.append(command);
                    } else {
                        String currentFormula = expressionBuilder.toString();
                        // Если в лейбе сложное выражение (например "15 + "), просто дописываем цифру в хвост
                        if (currentFormula.endsWith(" ")) {
                            expressionBuilder.append(command);
                        } else {
                            // Если мы руками набираем многоразрядное число (делаем из 7 -> 78)
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
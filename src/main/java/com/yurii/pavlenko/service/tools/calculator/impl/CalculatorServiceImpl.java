package com.yurii.pavlenko.service.tools.calculator.impl;

import com.yurii.pavlenko.service.tools.calculator.CalculatorService;

public class CalculatorServiceImpl implements CalculatorService {

    private final CalculatorMemory memoryManager = new CalculatorMemory();
    private final MathOperationEvaluator evaluator = new MathOperationEvaluator();
    private final ExpressionParser parser = new ExpressionParser(evaluator);

    @Override
    public void addToMemory(double value) {
        memoryManager.addToMemory(value);
    }

    @Override
    public void clearMemory() {
        memoryManager.clearMemory();
    }

    @Override
    public double getMemoryValue() {
        return memoryManager.getMemoryValue();
    }

    @Override
    public double calculateBinary(double firstOperand, double secondOperand, String operator) {
        return evaluator.calculateBinary(firstOperand, secondOperand, operator);
    }

    @Override
    public double calculateUnary(double operand, String operation, boolean isRadians) {
        return evaluator.calculateUnary(operand, operation, isRadians);
    }

    @Override
    public double calculateExpression(String expression, boolean isRadians) {
        return parser.parse(expression, isRadians);
    }
}
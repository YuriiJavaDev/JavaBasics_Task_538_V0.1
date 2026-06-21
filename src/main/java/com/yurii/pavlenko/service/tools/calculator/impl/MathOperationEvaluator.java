package com.yurii.pavlenko.service.tools.calculator.impl;

public class MathOperationEvaluator {

    public double calculateBinary(double firstOperand, double secondOperand, String operator) {
        return switch (operator) {
            case "+" -> firstOperand + secondOperand;
            case "-" -> firstOperand - secondOperand;
            case "*" -> firstOperand * secondOperand;
            case "/" -> {
                if (secondOperand == 0) {
                    throw new ArithmeticException("Cannot divide by zero");
                }
                yield firstOperand / secondOperand;
            }
            case "mod" -> firstOperand % secondOperand;
            case "x^y" -> Math.pow(firstOperand, secondOperand);
            default -> throw new IllegalArgumentException("Unknown binary operator: " + operator);
        };
    }

    public double calculateUnary(double operand, String operation, boolean isRadians) {
        double forwardAngle = isRadians ? operand : Math.toRadians(operand);

        return switch (operation) {
            case "%"    -> operand / 100.0;
            case "sin"  -> Math.sin(forwardAngle);
            case "cos"  -> Math.cos(forwardAngle);
            case "tan"  -> {
                double result = Math.tan(forwardAngle);
                if (Math.abs(result) > 1e10) {
                    throw new ArithmeticException("Undefined (Infinity)");
                }
                yield result;
            }
            case "asin" -> {
                if (operand < -1 || operand > 1) throw new ArithmeticException("Invalid asin input range [-1, 1]");
                double rawAsin = Math.asin(operand);
                yield isRadians ? rawAsin : Math.toDegrees(rawAsin);
            }
            case "acos" -> {
                if (operand < -1 || operand > 1) throw new ArithmeticException("Invalid acos input range [-1, 1]");
                double rawAcos = Math.acos(operand);
                yield isRadians ? rawAcos : Math.toDegrees(rawAcos);
            }
            case "atan" -> {
                double rawAtan = Math.atan(operand);
                yield isRadians ? rawAtan : Math.toDegrees(rawAtan);
            }
            case "ln"   -> {
                if (operand <= 0) throw new ArithmeticException("Invalid log input");
                yield Math.log(operand);
            }
            case "log"  -> {
                if (operand <= 0) throw new ArithmeticException("Invalid log input");
                yield Math.log10(operand);
            }
            case "exp"  -> Math.exp(operand);
            case "10^x" -> Math.pow(10, operand);
            case "sqrt" -> {
                if (operand < 0) throw new ArithmeticException("Invalid square root input");
                yield Math.sqrt(operand);
            }
            case "cbrt" -> Math.cbrt(operand);
            case "x²"   -> operand * operand;
            case "x³"   -> operand * operand * operand;
            case "1/x"  -> {
                if (operand == 0) throw new ArithmeticException("Cannot divide by zero");
                yield 1.0 / operand;
            }
            case "abs"  -> Math.abs(operand);
            case "n!"   -> calculateFactorial((int) operand);
            default -> throw new IllegalArgumentException("Unknown unary operation: " + operation);
        };
    }

    private double calculateFactorial(int n) {
        if (n < 0) throw new ArithmeticException("Invalid factorial input");
        double result = 1;
        for (int i = 1; i <= n; i++) {
            result *= i;
        }
        return result;
    }
}
package main.com.yurii.pavlenko.service.tools.calculator.impl;

import main.com.yurii.pavlenko.service.tools.calculator.CalculatorService;

/**
 * Robust implementation of CalculatorService using Java Math API.
 * Integrated with a high-performance recursive descent expression parser for structural bracket evaluation.
 */
public class CalculatorServiceImpl implements CalculatorService {

    private double memory = 0.0;

    @Override
    public void addToMemory(double value) {
        this.memory += value;
    }

    @Override
    public void clearMemory() {
        this.memory = 0.0;
    }

    @Override
    public double getMemoryValue() {
        return this.memory;
    }

    @Override
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

    @Override
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

    @Override
    public double calculateExpression(String expression) {
        // Очищаем строку от пробелов для корректной работы посимвольного парсера
        final String cleanExpr = expression.replaceAll("\\s+", "");

        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < cleanExpr.length()) ? cleanExpr.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < cleanExpr.length()) {
                    throw new ArithmeticException("Unexpected character: " + (char) ch);
                }
                return x;
            }

            // Сложение и вычитание
            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if      (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }

            // Умножение и деление
            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if      (eat('*')) x *= parseFactor();
                    else if (eat('/')) {
                        double divisor = parseFactor();
                        if (divisor == 0) {
                            throw new ArithmeticException("Cannot divide by zero");
                        }
                        x /= divisor;
                    }
                    else return x;
                }
            }

            // Унарные знаки, скобки, числа и степени
            double parseFactor() {
                if (eat('+')) return parseFactor();
                if (eat('-')) return -parseFactor();

                double x;
                int startPos = this.pos;
                if (eat('(')) {
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(cleanExpr.substring(startPos, this.pos));
                } else {
                    throw new ArithmeticException("Unknown expression format");
                }

                if (eat('^')) x = Math.pow(x, parseFactor());

                return x;
            }
        }.parse();
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
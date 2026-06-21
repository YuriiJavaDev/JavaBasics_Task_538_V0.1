package com.yurii.pavlenko.service.tools.calculator.impl;

public class ExpressionParser {

    private final MathOperationEvaluator evaluator;
    private String cleanExpr;
    private int pos;
    private int ch;

    public ExpressionParser(MathOperationEvaluator evaluator) {
        this.evaluator = evaluator;
    }

    public double parse(String expression, boolean isRadians) {
        this.cleanExpr = expression.replaceAll("\\s+", "");
        this.pos = -1;
        nextChar();
        double x = parseExpression(isRadians);
        if (pos < cleanExpr.length()) {
            throw new ArithmeticException("Unexpected character: " + (char) ch);
        }
        return x;
    }

    private void nextChar() {
        ch = (++pos < cleanExpr.length()) ? cleanExpr.charAt(pos) : -1;
    }

    private boolean eat(int charToEat) {
        while (ch == ' ') nextChar();
        if (ch == charToEat) {
            nextChar();
            return true;
        }
        return false;
    }

    private double parseExpression(boolean isRadians) {
        double x = parseTerm(isRadians);
        for (; ; ) {
            if (eat('+')) x += parseTerm(isRadians);
            else if (eat('-')) x -= parseTerm(isRadians);
            else return x;
        }
    }

    private double parseTerm(boolean isRadians) {
        double x = parseFactor(isRadians);
        for (; ; ) {
            if (eat('*')) x *= parseFactor(isRadians);
            else if (eat('/')) {
                double divisor = parseFactor(isRadians);
                if (divisor == 0) {
                    throw new ArithmeticException("Cannot divide by zero");
                }
                x /= divisor;
            } else return x;
        }
    }

    private double parseFactor(boolean isRadians) {
        if (eat('+')) return parseFactor(isRadians);
        if (eat('-')) return -parseFactor(isRadians);

        double x;
        int startPos = this.pos;

        if (eat('(')) {
            x = parseExpression(isRadians);
            eat(')');
        } else if ((ch >= '0' && ch <= '9') || ch == '.') {
            while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
            x = Double.parseDouble(cleanExpr.substring(startPos, this.pos));
        } else if ((ch >= 'a' && ch <= 'z') || ch == '!') {
            while (ch >= 'a' && ch <= 'z') nextChar();
            String func = cleanExpr.substring(startPos, this.pos);

            if (eat('(')) {
                double argument = parseExpression(isRadians);
                eat(')');
                x = evaluator.calculateUnary(argument, func, isRadians);
            } else {
                throw new ArithmeticException("Missing parenthesis after function: " + func);
            }
        } else {
            throw new ArithmeticException("Unknown expression format");
        }

        if (eat('!')) {
            x = evaluator.calculateUnary(x, "n!", false);
        }

        if (eat('^')) x = Math.pow(x, parseFactor(isRadians));

        return x;
    }
}